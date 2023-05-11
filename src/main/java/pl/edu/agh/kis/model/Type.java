package pl.edu.agh.kis.model;

import java.util.Objects;

public class Type {
    public String name;

    private Type(String name) {
        this.name = name;
    }

    public final static Type Integer = new Type("integer");
    public final static Type Floating = new Type("floating");
    public final static Type String = new Type("string");
    public final static Type Boolean = new Type("boolean");
    public final static Type Object = new Type("object");

    public final static Type Void = new Type("void");

    public static Type of(String name) {
        return switch (name.toLowerCase()) {
            case "integer", "int" -> Type.Integer;
            case "floating", "float" -> Type.Floating;
            case "boolean", "bool" -> Type.Boolean;
            case "string", "str" -> Type.String;
            case "object", "obj" -> Type.Object;
            default -> new Type(name);
        };
    }

    public String getParamName() {
        return name.toLowerCase().charAt(0) + name.chars().skip(1)
                   .filter(Character::isUpperCase)
                   .map(Character::toLowerCase)
                   .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                   .toString();
    }

    @Override
    public String toString() {
        return name;
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
