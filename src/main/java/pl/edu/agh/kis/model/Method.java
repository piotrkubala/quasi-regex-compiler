package pl.edu.agh.kis.model;

import java.util.*;

public class Method {
    public String name;
    public List<Type> parameterTypes;
    public Type returnType;

    public Method(String name, List<Type> parameterTypes, Type returnType) {
        this.name = name;
        this.parameterTypes = parameterTypes;
        this.returnType = returnType;
    }

    public String getName() {
        return name;
    }

    public List<Type> getParameterTypes() {
        return parameterTypes;
    }

    public List<String> getParameterNames() {
        Map<String, Integer> namesCount = new HashMap<>();
        List<String> names = new ArrayList<>();

        for (var param : parameterTypes) {
            String paramName = param.getParamName();
            namesCount.put(paramName, namesCount.getOrDefault(paramName, -1) + 1);

            names.add(paramName + namesCount.get(paramName));
        }

        return names;
    }

    public Type getReturnType() {
        return returnType;
    }

    public boolean isProcedure() {
        return returnType == Type.Void;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Method method = (Method) o;
        return name.equals(method.name) && parameterTypes.equals(method.parameterTypes) && returnType.equals(method.returnType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, parameterTypes, returnType);
    }
}
