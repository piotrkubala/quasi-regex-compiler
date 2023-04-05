package pl.edu.agh.kis;

import java.util.List;
import java.util.Objects;

public class Method {
    public static class Type {
        public String name;

        private Type(String name) {
            this.name = name;
        }

        public final static Type Integer = new Type("INTEGER");
        public final static Type Floating = new Type("FLOATING");
        public final static Type String = new Type("STRING");
        public final static Type Boolean = new Type("BOOLEAN");
        public final static Type Object = new Type("OBJECT");

        public final static Type Void = new Type("VOID");

        public static Type of(String name) {
            return switch (name.toUpperCase()) {
                case "INTEGER", "INT" -> Type.Integer;
                case "FLOATING", "FLOAT" -> Type.Floating;
                case "BOOLEAN", "BOOL" -> Type.Boolean;
                case "STRING", "STR" -> Type.String;
                case "OBJECT", "OBJ" -> Type.Object;
                default -> new Type(name);
            };
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Type type = (Type) o;
            return Objects.equals(name, type.name);
        }

        @Override
        public int hashCode() {
            return Objects.hash(name);
        }
    }

    public String name;
    public List<Type> parameterTypes;
    public Type returnType;

    public Method(String name, List<Type> parameterTypes, Type returnType) {
        this.name = name;
        this.parameterTypes = parameterTypes;
        this.returnType = returnType;
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
