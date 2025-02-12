package name.ncg777.computing;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;
import java.lang.reflect.Constructor;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

import name.ncg777.maths.sequences.Sequence;

@Command(name = "enum-printer", mixinStandardHelpOptions = true, 
         description = "Print elements of any Enumeration class")
public class ReflectiveEnumerationPrinter implements Callable<Integer> {

    @Parameters(index = "0", description = "Fully qualified class name of the Enumeration")
    private String className;

    @Parameters(index = "1..*", description = "Parameters for the Enumeration constructor")
    private String[] params;

    @Override
    public Integer call() throws Exception {
        // Load the class
        Class<?> enumClass = Class.forName(className);
        
        // Find the appropriate constructor
        Constructor<?>[] constructors = enumClass.getConstructors();
        Constructor<?> constructor = findMatchingConstructor(constructors, params);
        
        if (constructor == null) {
            System.err.println("No matching constructor found for the given parameters.");
            return 1;
        }

        // Convert parameters to appropriate types
        Object[] convertedParams = convertParams(constructor.getParameterTypes(), params);
        
        // Create an instance of the Enumeration
        Enumeration<?> enumeration = (Enumeration<?>) constructor.newInstance(convertedParams);

        // Print all elements
        while (enumeration.hasMoreElements()) {
            Object element = enumeration.nextElement();
            System.out.println(prettyPrint(element));
        }

        return 0;
    }

    private Constructor<?> findMatchingConstructor(Constructor<?>[] constructors, String[] params) {
        for (Constructor<?> constructor : constructors) {
            if (constructor.getParameterCount() == params.length) {
                return constructor;
            }
        }
        return null;
    }

    private Object[] convertParams(Class<?>[] paramTypes, String[] params) {
        Object[] convertedParams = new Object[params.length];
        for (int i = 0; i < params.length; i++) {
            convertedParams[i] = convertParam(paramTypes[i], params[i]);
        }
        return convertedParams;
    }

    private Object convertParam(Class<?> paramType, String param) {
        if (paramType == String.class) {
            return param;
        } else if (paramType == int.class || paramType == Integer.class) {
            return Integer.parseInt(param);
        } else if (paramType == long.class || paramType == Long.class) {
            return Long.parseLong(param);
        } else if (paramType == double.class || paramType == Double.class) {
            return Double.parseDouble(param);
        } else if (paramType == boolean.class || paramType == Boolean.class) {
            return Boolean.parseBoolean(param);
        } else if (paramType == int[].class) {
          Sequence _s = Sequence.parse(param);
          int[] o = new int[_s.size()];
          for(int i=0;i<o.length;i++) o[i] = _s.get(i);
          return o;
        } else if (paramType == Integer[].class) {
            return ((Integer[])Sequence.parse(param).toArray());
        } else if (paramType == List.class) {
            return Sequence.parse(param);
        }
        // Add more type conversions as needed
        throw new IllegalArgumentException("Unsupported parameter type: " + paramType);
    }

    private String prettyPrint(Object obj) {
        if (obj == null) {
            return "null";
        }
        if (obj instanceof int[]) {
            return (new Sequence((int[])obj)).toString();
        }
        if (obj instanceof Integer[]) {
          return (new Sequence((Integer[])obj)).toString();
        }
        if (obj instanceof Sequence) {
          return ((Sequence)obj).toString(true);
        }
        if (obj instanceof List) {
            return ((List<?>) obj).stream()
                .map(this::prettyPrint)
                .collect(Collectors.joining(" "));
        }
        if (obj instanceof Set) {
            return ((Set<?>) obj).stream()
                .map(this::prettyPrint)
                .collect(Collectors.joining(" "));
        }
        if (obj.getClass().isArray()) {
            return Arrays.deepToString((Object[]) obj);
        }
        return obj.toString();
    }

    public static void main(String[] args) {
        int exitCode = new CommandLine(new ReflectiveEnumerationPrinter()).execute(args);
        System.exit(exitCode);
    }
}