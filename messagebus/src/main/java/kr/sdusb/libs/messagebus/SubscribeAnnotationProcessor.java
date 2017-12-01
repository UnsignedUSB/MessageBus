package kr.sdusb.libs.messagebus;


import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.tools.JavaFileObject;

@SupportedAnnotationTypes({"kr.sdusb.libs.messagebus.ListSubscriber", "kr.sdusb.libs.messagebus.Subscribe"})
@SupportedSourceVersion(SourceVersion.RELEASE_7)
public class SubscribeAnnotationProcessor extends AbstractProcessor{
    private List<ClassInfo> classInfos = new ArrayList<ClassInfo>();
    private Set<String> listSubscribers = new HashSet<>();
    private HashMap<Integer, List<MethodInfo>> map = new HashMap<Integer, List<MethodInfo>>();

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnv) {
        if(roundEnv == null || roundEnv.processingOver() || roundEnv.getRootElements().size() == 0) {
            return false;
        }
        Collection<? extends Element> annotatedElements1 = roundEnv.getElementsAnnotatedWith(ListSubscriber.class);
        Collection<? extends Element> annotatedElements2 = roundEnv.getElementsAnnotatedWith(Subscribe.class);
        System.out.println("[MessageBus] Check Annotated Classes List Size = " + (annotatedElements1.size() + annotatedElements2.size()));

        if(annotatedElements1.size() + annotatedElements2.size() == 0) {
            System.out.println("###########################################");
            return false;
        }

        long startTime = System.currentTimeMillis();

        System.out.println("###########################################");
        System.out.println("##            MessageBus Build           ##");
        System.out.println("###########################################");
        System.out.println("[MessageBus] Start Process : " + roundEnv);
        setClassInfosAndMethodInfos(roundEnv);

        StringBuilder builder = new StringBuilder()
                .append("package kr.sdusb.libs.messagebus;\n\n")
                .append( getImportStrings() ).append("\n")   // import
                .append("public class MessageBus {\n\n")   // open class
                .append( getSingleTonString() )
                .append( getConstructorString() )
                .append( getVariablesString() ).append("\n")
                .append( getRegisterPartString() )
                .append( getUnregisterPartString() )
                .append( getEventHandleMethodString() )
                .append( getMethodDefinitionString() )
                .append("}\n"); // close class

        try { // write the file
            JavaFileObject source = processingEnv.getFiler().createSourceFile("kr.sdusb.libs.messagebus.MessageBus");

            Writer writer = source.openWriter();
            writer.write(builder.toString());
            writer.flush();
            writer.close();
        } catch (IOException e) {
            // Note: calling e.printStackTrace() will print IO errors
            // that occur from the file already existing after its first run, this is normal
        }

//        writeMessageBoard();

        long duration = System.currentTimeMillis() - startTime;

        int sec = (int) (duration / 1000);
        int msec = (int) (duration % 1000);
        System.out.println("[MessageBus] Finish Process : " + sec +"." + msec +"s");
        System.out.println("###########################################");
        return true;
    }

    private void writeMessageBoard() {
        try{
            List<Integer> eventsList = new ArrayList<Integer>();
            eventsList.addAll(map.keySet());
            Collections.sort(eventsList);

            BufferedWriter out = new BufferedWriter(new FileWriter("MessageBoard.csv"));
//            String s = "File Text";
//
//            out.write(s);
//            out.newLine();
//            out.write(s);
//            out.newLine();

            for(int event : eventsList) {
                out.write(Integer.toHexString(event));
                out.newLine();

                List<MethodInfo> methodInfoList = map.get(event);
                for(MethodInfo mi : methodInfoList) {
                    out.write(",");
                    if(mi.classInfo != null && mi.classInfo.classNameWithPackage != null) {
                        out.write("\""+mi.classInfo.className +"\n"+mi.classInfo.classNameWithPackage+"\"");
                    }
                    out.write(",");
                    out.write(mi.methodName);
                    out.write(",");
                    if(mi.paramClassNameWithPackage != null) {
                        String name = mi.paramClassNameWithPackage.substring(mi.paramClassNameWithPackage.lastIndexOf(".")+1);
                        out.write("\""+name +"\n"+mi.paramClassNameWithPackage+"\"");
                    }

                    out.newLine();
                }
                out.newLine();
            }

            out.close();
        }catch (Exception e) {
            e.printStackTrace();
        }
    }


    private boolean hasMessageParam(Element element) {
        ExecutableElement methodElement = (ExecutableElement) element;
        return methodElement.getParameters() != null && methodElement.getParameters().size() == 1;
    }

    private void setClassInfosAndMethodInfos(RoundEnvironment roundEnv) {
        for (Element element : roundEnv.getElementsAnnotatedWith(ListSubscriber.class)) {
            System.out.println("[MessageBus] (ListSubscriber) = " + element);
            listSubscribers.add(element.toString());
        }


        for (Element element : roundEnv.getElementsAnnotatedWith(Subscribe.class)) {
            System.out.println("[MessageBus] (Subscribe) = " + element);
            String methodName = element.getSimpleName().toString();
            TypeElement classElement = (TypeElement) element.getEnclosingElement();
            String classPackageName = classElement.toString();
            boolean isListSubscriber = false;

            for(AnnotationMirror am:classElement.getAnnotationMirrors()) {
                if(am.getAnnotationType().toString().equals(ListSubscriber.class.getCanonicalName())) {
                    isListSubscriber = true;
                }
            }

            List<String> superClasses = new ArrayList<String>();
            while( classElement.getSuperclass() != null && classElement.getSuperclass().toString().equals("java.lang.Object") == false ) {
                TypeMirror mirror = classElement.getSuperclass();
                superClasses.add(mirror.toString().trim());
                classElement = ((TypeElement)((DeclaredType)mirror).asElement());
            }



            if(element.getModifiers().contains(Modifier.PUBLIC) == false) {
                throw new RuntimeException("SubscribeAnnotationProcessor : Not supported Method type. Method must be public.  \n"+classPackageName+"."+methodName);
            }

            ExecutableElement methodElement = (ExecutableElement) element;
            String paramClassNameWithPackage = null;
            if(methodElement.getParameters() != null && methodElement.getParameters().size() > 1) {
                throw new RuntimeException("SubscribeAnnotationProcessor : Not supported Method type. Method can have only one parameter.  \n"+classPackageName+"."+methodName);
            }

            if(methodElement.getParameters() != null && methodElement.getParameters().size() == 1) {
                paramClassNameWithPackage = methodElement.getParameters().get(0).asType().toString();
            }

            List<String> throwns = null;
            if(methodElement.getThrownTypes() != null && methodElement.getThrownTypes().size() > 0) {
                throwns = new ArrayList<String>();
                for(TypeMirror tm : methodElement.getThrownTypes()) {
                    throwns.add(tm.toString());
                }
            }

            Subscribe annotation = element.getAnnotation(Subscribe.class);
            try {

                boolean hasMessageParam = hasMessageParam(element);
                int thread = annotation.thread();
                int[] events = annotation.events();
                int priority = annotation.priority();
                boolean ignoreCastException = annotation.ignoreCastException();
                boolean receiveEventType = false;//annotation.receiveEventType();

                MethodInfo methodInfo = new MethodInfo(classPackageName, methodName, paramClassNameWithPackage, hasMessageParam, events, thread, throwns, superClasses, ignoreCastException, receiveEventType);
                methodInfo.setPriority(priority);
                methodInfo.classInfo.hasManyInstance = isListSubscriber;
                if(classInfos.contains(methodInfo.classInfo) == false) {
                    classInfos.add(methodInfo.classInfo);
                }


                for(int event:events) {
                    List<MethodInfo> list = map.get(event);
                    if(list == null) {
                        list = new ArrayList<MethodInfo>();
                        map.put(event, list);
                    }

                    list.add(methodInfo);
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        for(ClassInfo ci:classInfos) {
            ci.hasManyInstance = ci.hasManyInstance || listSubscribers.contains(ci.classNameWithPackage);
        }

        sortClassInfosList();
        sortMethodInfoList();
    }

    private void sortMethodInfoList() {
        for(int event:map.keySet()) {
            List<MethodInfo> list = map.get(event);
            Collections.sort(list);
        }
    }

    private void sortClassInfosList() {
        List<ClassInfo> newClassInfos = new ArrayList<ClassInfo>(classInfos);
        classInfos.clear();
        while(newClassInfos.size() > 0) {
            ClassInfo ci = newClassInfos.remove(0);

            for(int i=0; i < classInfos.size() ; i++) {
                ClassInfo checkCi = classInfos.get(i);
                if(checkCi.equals(ci)) {
                    break;
                }

                if(ci.superClasses.contains(checkCi.classNameWithPackage)) {
                    classInfos.add(i, ci);
                    ci = null;
                    break;
                }
            }

            if(ci != null) {
                classInfos.add(ci);
            }
        }
    }

    private String getSingleTonString() {
        StringBuilder sb = new StringBuilder();

        sb.append("\tprivate static MessageBus INSTANCE = null;\n" +
                "\tpublic static MessageBus getInstance() {\n" +
                "\t\tif(INSTANCE == null) {\n" +
                "\t\t\tINSTANCE = new MessageBus();\n" +
                "\t\t}\n" +
                "\t\treturn INSTANCE;" +
                "\t}\n");

        return sb.toString();
    }

    private String getConstructorString() {
        StringBuilder sb = new StringBuilder();

        sb.append("\tprivate MessageBus() {\n" +
                "\t\thandler = new Handler(Looper.getMainLooper());\n" +
                "\t}\n");

        return sb.toString();
    }

    private String getImportStrings() {
        System.out.println("[MessageBus] Writing Imports");
        StringBuilder sb = new StringBuilder();

        sb.append("import android.os.Handler;\nimport android.os.Looper;\nimport java.lang.Thread;\nimport java.lang.Runnable;\nimport java.util.ArrayList;\nimport java.util.List;\n");
        for(ClassInfo ci:classInfos) {
            if(ci.classNameWithPackage != null) {
                sb.append("import ").append(ci.classNameWithPackage).append(";\n");
            }
        }

        return sb.toString();
    }

    private String getVariablesString() {
        System.out.println("[MessageBus] Writing Variables");
        StringBuilder sb = new StringBuilder();

        sb.append("\tprivate Handler handler;\n");
        for(ClassInfo ci:classInfos) {
            if(ci.hasManyInstance) {
                sb.append("\tprivate List<").append(ci.className).append("> ").append(ci.className.toLowerCase()).append(" = new ArrayList<>();\n");
            } else {
                sb.append("\tprivate ").append(ci.className).append(" ").append(ci.className.toLowerCase()).append(";\n");
            }
        }

        return sb.toString();
    }

    private String getRegisterPartString() {
        System.out.println("[MessageBus] Writing regist() Method");
        StringBuilder sb = new StringBuilder();

        sb.append("\tpublic void register(Object model) {\n\t\t");
        for(ClassInfo ci:classInfos) {
            sb.append("if (model instanceof ").append(ci.classNameWithPackage).append(") {\n");

            if(ci.hasManyInstance) {
                sb.append("\t\t\t").append(ci.className.toLowerCase()).append(".add((").append(ci.classNameWithPackage).append(")model);\n");
            } else {
                sb.append("\t\t\t").append(ci.className.toLowerCase()).append(" = (").append(ci.classNameWithPackage).append(")model;\n");
            }
            sb.append("\t\t} else ");
        }
        sb.append("{}\n")
                .append("\t}\n\n");

        return sb.toString();
    }


    private String getUnregisterPartString() {
        System.out.println("[MessageBus] Writing unregist() Method");
        StringBuilder sb = new StringBuilder();

        sb.append("\tpublic void unregister(Object model) {\n\t\t");
        for(ClassInfo ci:classInfos) {
            sb.append("if (model instanceof ").append(ci.classNameWithPackage).append(") {\n");
            if(ci.hasManyInstance) {
                sb.append("\t\t\t").append(ci.className.toLowerCase()).append(".remove((").append(ci.classNameWithPackage).append(")model);\n");
            } else {
                sb.append("\t\t\t").append(ci.className.toLowerCase()).append(" = null;\n");
            }
            sb.append("\t\t} else ");
        }
        sb.append("{}\n")
                .append("\t}\n\n");

        return sb.toString();
    }



    private String getEventHandleMethodString() {
        System.out.println("[MessageBus] Writing handle() Method");
        StringBuilder sb = new StringBuilder();

        sb.append("\tpublic void handle(int what, Object data) {\n")
                .append("\t\tswitch(what) {\n");
        for(int event:map.keySet()) {
            sb.append("\t\t\tcase ").append(event).append(":\n");
            List<MethodInfo> infoList = map.get(event);

            List<String> methodStringList = new ArrayList<String>();
            for(MethodInfo info : infoList) {
                String methodString = null;
                if(info.ignoreCastException) {
                    methodString = info.hasMessageParam
                            ? String.format("\t\t\t\ttry{%s((%s)data);}catch(ClassCastException e){}\n", getEventHandlerMethodName(info, event), info.paramClassNameWithPackage)
                            : String.format("\t\t\t\ttry{%s();}catch(ClassCastException e){}\n", getEventHandlerMethodName(info, event));
                } else {
                    methodString = info.hasMessageParam
                            ? String.format("\t\t\t\t%s((%s)data);\n", getEventHandlerMethodName(info, event), info.paramClassNameWithPackage)
                            : String.format("\t\t\t\t%s();\n", getEventHandlerMethodName(info, event));
                }
                if(methodStringList.contains(methodString) == false) {
                    methodStringList.add(methodString);
                }
            }

            for(String methodString:methodStringList) {
                sb.append(methodString);
            }
            sb.append("\t\t\t\tbreak;\n");
        }
        sb.append("\t\t}\n")        // end Switch
                .append("\t}\n");   // end Method

        return sb.toString();
    }


    private String getMethodDefinition_MainThread(MethodInfo methodInfo) {
        System.out.println("[MessageBus] Writing private Method for MainThread : " + getEventHandlerMethodName(methodInfo, -1));
        StringBuilder sb = new StringBuilder();

        sb.append("\t\tif(Thread.currentThread() == Looper.getMainLooper().getThread()) {\n");

        String defaultTabs = "\t\t\t";
        if(methodInfo.throwns != null && methodInfo.throwns.size() > 0) {
            sb.append(defaultTabs).append("try{\n");
            defaultTabs += "\t";
        }

        if(methodInfo.classInfo.hasManyInstance) {
            sb.append(defaultTabs).append("for(").append(methodInfo.classInfo.className).append(" classModel:").append(methodInfo.classInfo.className.toLowerCase()).append(") {\n")
                    .append(defaultTabs).append("\tclassModel.").append(methodInfo.methodName).append(methodInfo.hasMessageParam ? "(data);\n" : "();\n")
                    .append(defaultTabs).append("}\n");
        } else {
            sb.append(defaultTabs).append("if(").append(methodInfo.classInfo.className.toLowerCase()).append(" != null) {\n")
                    .append(defaultTabs).append("\t").append(methodInfo.classInfo.className.toLowerCase()).append(".").append(methodInfo.methodName).append(methodInfo.hasMessageParam ? "(data);\n" : "();\n")
                    .append(defaultTabs).append("}\n");
        }

        defaultTabs = "\t\t\t";
        if(methodInfo.throwns != null && methodInfo.throwns.size() > 0) {
            sb.append(defaultTabs).append("}\n");
            int count = 0;
            for(String throwString : methodInfo.throwns) {
                sb.append(defaultTabs).append("catch (").append(throwString).append(" e").append(count).append("){}\n");
                count++;
            }
        }


        sb.append("\t\t} else {\n")
                .append("\t\t\thandler.post(new Runnable(){\n\t\t\t\t@Override public void run() {\n");

        defaultTabs = "\t\t\t\t\t";
        if(methodInfo.throwns != null && methodInfo.throwns.size() > 0) {
            sb.append(defaultTabs).append("try{\n");
            defaultTabs += "\t";
        }

        if(methodInfo.classInfo.hasManyInstance) {
            sb.append(defaultTabs).append("for(").append(methodInfo.classInfo.className).append(" classModel:").append(methodInfo.classInfo.className.toLowerCase()).append(") {\n")
                    .append(defaultTabs).append("\tclassModel.").append(methodInfo.methodName).append(methodInfo.hasMessageParam ? "(data);\n" : "();\n")
                    .append(defaultTabs).append("}\n");
        } else {
            sb.append(defaultTabs).append("if(").append(methodInfo.classInfo.className.toLowerCase()).append(" != null) {\n")
                    .append(defaultTabs).append("\t").append(methodInfo.classInfo.className.toLowerCase()).append(".").append(methodInfo.methodName).append(methodInfo.hasMessageParam ? "(data);\n" : "();\n")
                    .append(defaultTabs).append("}\n");
        }

        defaultTabs = "\t\t\t\t\t";
        if(methodInfo.throwns != null && methodInfo.throwns.size() > 0) {
            sb.append(defaultTabs).append("}\n");
            int count = 0;
            for(String throwString : methodInfo.throwns) {
                sb.append(defaultTabs).append("catch (").append(throwString).append(" e").append(count).append("){}\n");
                count++;
            }
        }
        sb.append("\t\t\t\t}\n")
                .append("\t\t\t});\n")
                .append("\t\t}\n")
                .append("\t}\n\n");

        return sb.toString();
    }

    private String getMethodDefinition_CurrentThread(MethodInfo methodInfo) {
        System.out.println("[MessageBus] Writing private Method for Current Thread : " + getEventHandlerMethodName(methodInfo, -1));
        StringBuilder sb = new StringBuilder();

        String defaultTabs = "\t\t";
        if(methodInfo.throwns != null && methodInfo.throwns.size() > 0) {
            sb.append(defaultTabs).append("try{\n");
            defaultTabs += "\t";
        }

        if(methodInfo.classInfo.hasManyInstance) {
            sb.append(defaultTabs).append("for(").append(methodInfo.classInfo.className).append(" classModel:").append(methodInfo.classInfo.className.toLowerCase()).append(") {\n")
                    .append(defaultTabs).append("\tclassModel.").append(methodInfo.methodName).append(methodInfo.hasMessageParam ? "(data);\n" : "();\n")
                    .append(defaultTabs).append("}\n");
        } else {
            sb.append(defaultTabs).append("if(").append(methodInfo.classInfo.className.toLowerCase()).append(" != null) {\n")
                    .append(defaultTabs).append("\t").append(methodInfo.classInfo.className.toLowerCase()).append(".").append(methodInfo.methodName).append(methodInfo.hasMessageParam ? "(data);\n" : "();\n")
                    .append(defaultTabs).append("}\n");
        }

        defaultTabs = "\t\t";
        if(methodInfo.throwns != null && methodInfo.throwns.size() > 0) {
            sb.append(defaultTabs).append("}\n");
            int count = 0;
            for(String throwString : methodInfo.throwns) {
                sb.append(defaultTabs).append("catch (").append(throwString).append(" e").append(count).append("){}\n");
                count++;
            }
        }
        sb.append("\t}\n\n");

        return sb.toString();
    }


    private String getMethodDefinition_NewThread(MethodInfo methodInfo) {
        System.out.println("[MessageBus] Writing private Method for New Thread : " + getEventHandlerMethodName(methodInfo, -1));
        StringBuilder sb = new StringBuilder();
        String defaultTabs;
        sb.append("\t\tnew java.lang.Thread(new Runnable(){\n\t\t\t@Override public void run() {\n");

        defaultTabs = "\t\t\t\t";
        if(methodInfo.throwns != null && methodInfo.throwns.size() > 0) {
            sb.append(defaultTabs).append("try{\n");
            defaultTabs += "\t";
        }

        if(methodInfo.classInfo.hasManyInstance) {
            sb.append(defaultTabs).append("for(").append(methodInfo.classInfo.className).append(" classModel:").append(methodInfo.classInfo.className.toLowerCase()).append(") {\n")
                    .append(defaultTabs).append("\tclassModel.").append(methodInfo.methodName).append(methodInfo.hasMessageParam ? "(data);\n" : "();\n")
                    .append(defaultTabs).append("}\n");
        } else {
            sb.append(defaultTabs).append("if(").append(methodInfo.classInfo.className.toLowerCase()).append(" != null) {\n")
                    .append(defaultTabs).append("\t").append(methodInfo.classInfo.className.toLowerCase()).append(".").append(methodInfo.methodName).append(methodInfo.hasMessageParam ? "(data);\n" : "();\n")
                    .append(defaultTabs).append("}\n");
        }

        defaultTabs = "\t\t\t\t";
        if(methodInfo.throwns != null && methodInfo.throwns.size() > 0) {
            sb.append(defaultTabs).append("}\n");
            int count = 0;
            for(String throwString : methodInfo.throwns) {
                sb.append(defaultTabs).append("catch (").append(throwString).append(" e").append(count).append("){}\n");
                count++;
            }
        }
        sb.append("\t\t\t}\n")
                .append("\t\t}).start();\n")
                .append("\t}\n\n");


        return sb.toString();
    }

    private String getMethodDefinitionString() {
        StringBuilder sb = new StringBuilder();
        for(int event:map.keySet()) {
            for(MethodInfo methodInfo:map.get(event)) {
                System.out.println("[MessageBus] Writing private Method : " + getEventHandlerMethodName(methodInfo, event));
                sb.append("\tprivate void ").append(getEventHandlerMethodName(methodInfo, event));

                if(methodInfo.hasMessageParam) {
                    sb.append("(");
                    if(methodInfo.threadType != ThreadType.CURRENT) {
                        sb.append("final ");
                    }
                    sb.append(methodInfo.paramClassNameWithPackage).append(" data) {\n");
                } else {
                    sb.append("() {\n");
                }

                switch (methodInfo.threadType) {
                    case ThreadType.MAIN:
                        sb.append(getMethodDefinition_MainThread(methodInfo));
                        break;
                    case ThreadType.CURRENT:
                        sb.append(getMethodDefinition_CurrentThread(methodInfo));
                        break;
                    case ThreadType.NEW:
                        sb.append(getMethodDefinition_NewThread(methodInfo));
                        break;
                }
            }
        }

        return sb.toString();
    }

    private String getEventHandlerMethodName(MethodInfo info, int event) {
        StringBuilder builder = new StringBuilder();

        builder.append("handleEvent_").append(info.classInfo.classNameWithPackage.replaceAll("[.]","_")).append("_").append(info.methodName).append("_").append(Integer.toHexString(event));

        return builder.toString();
    }


    private class ClassInfo {
        public final String classNameWithPackage;
        public final String className;
        public final List<String> superClasses;
        public boolean hasManyInstance = false;

        private ClassInfo(String classNameWithPackage, List<String> superClasses) {
            this.superClasses = superClasses;

            if(classNameWithPackage.equals("boolean")) {
                this.classNameWithPackage = null;
                className = classNameWithPackage;
            } else if(classNameWithPackage.equals("int")) {
                this.classNameWithPackage = null;
                className = classNameWithPackage;
            } else if(classNameWithPackage.equals("byte")) {
                this.classNameWithPackage = null;
                className = classNameWithPackage;
            } else if(classNameWithPackage.equals("char")) {
                this.classNameWithPackage = null;
                className = classNameWithPackage;
            } else if(classNameWithPackage.equals("short")) {
                this.classNameWithPackage = null;
                className = classNameWithPackage;
            } else if(classNameWithPackage.equals("long")) {
                this.classNameWithPackage = null;
                className = classNameWithPackage;
            } else if(classNameWithPackage.equals("float")) {
                this.classNameWithPackage = null;
                className = classNameWithPackage;
            } else if(classNameWithPackage.equals("double")) {
                this.classNameWithPackage = null;
                className = classNameWithPackage;
            } else {
                this.classNameWithPackage = classNameWithPackage;
                this.className = classNameWithPackage.substring(classNameWithPackage.lastIndexOf(".") + 1);
            }
        }

        @Override
        public boolean equals(Object o) {
            if(o instanceof ClassInfo) {
                ClassInfo ci = (ClassInfo)o;
                return (ci.classNameWithPackage == null && classNameWithPackage == null && ci.className.equals(className)) || (ci.classNameWithPackage != null && classNameWithPackage != null && ci.classNameWithPackage.equals(classNameWithPackage) );
            }
            return false;
        }

    }

    private class MethodInfo implements Comparable<MethodInfo>{
        public int priority = Integer.MAX_VALUE;
        public final ClassInfo classInfo;
        public final String methodName;
        public final String paramClassNameWithPackage;
        public final boolean hasMessageParam;
        public final int[] values;
        public final @ThreadType int threadType;
        public final List<String> throwns;
        public final boolean ignoreCastException;
        public final boolean receiveEventType;

        private MethodInfo(String classNameWithPackage, String methodName, String paramClassNameWithPackage, boolean hasMessageParam, int[] values, @ThreadType int threadType, List<String> throwns, List<String> superClasses, boolean ignoreCastException, boolean receiveEventType) {
            this.ignoreCastException = ignoreCastException;
            this.receiveEventType = receiveEventType;
            this.classInfo = new ClassInfo(classNameWithPackage.trim(), superClasses);
            this.methodName = methodName;
            this.threadType = threadType;
            this.throwns = throwns;
            this.paramClassNameWithPackage = paramClassNameWithPackage;
            this.hasMessageParam = hasMessageParam;
            this.values = values;
        }

        private void setPriority(int priority) {
            this.priority = priority;
        }

        @Override
        public boolean equals(Object o) {
            if(o instanceof MethodInfo) {
                MethodInfo mi = (MethodInfo)o;
                return mi.classInfo.equals(classInfo) && mi.methodName.equals(methodName);
            }
            return false;
        }

        @Override
        public int compareTo(MethodInfo methodInfo) {
            return priority - methodInfo.priority;
        }
    }
}
