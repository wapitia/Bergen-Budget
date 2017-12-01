
package com.wapitia.sched.parse.node;

import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SchedNodeContainerBase {

    private String      containerName;
    private SchedNode[] nodes;

    public SchedNodeContainerBase(String containerName, SchedNode... nodes) {
        this.containerName = containerName;
        this.nodes = nodes;
    }

    protected Stream<SchedNode> nodeStream() {

        return Arrays.<SchedNode> stream(nodes);
    }

    @Override
    public String toString() {

        String result = Arrays.asList(nodes).stream().map(Object::toString)
            .collect(Collectors.joining(",", containerName + "(", ")"));
        return result;
    }

}
