
package com.wapitia.sched.parse.node;

import java.util.stream.IntStream;

public class DomSpecsNode extends SchedNodeContainerBase implements SchedNode {

    public DomSpecsNode(SchedNode... nodes) {
        super("DomSpec", nodes);
    }

    public IntStream daysOfMonthStream() {

        final IntStream result = nodeStream()
            .filter((SchedNode snode) -> snode instanceof DaysOfMonthNode)
            .map((SchedNode snode) -> (DaysOfMonthNode) snode)
            .flatMapToInt(DaysOfMonthNode::daysOfMonthStream);
        return result;
    }
}
