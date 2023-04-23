package pl.edu.agh.kis.model;

import java.util.Set;

public class Model {
    public Node mainBody;
    public Set<Method> methods;

    public Model(Node mainBody, Set<Method> methods) {
        this.mainBody = mainBody;
        this.methods = methods;
    }
}
