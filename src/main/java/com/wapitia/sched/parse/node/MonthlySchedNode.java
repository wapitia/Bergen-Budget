
package com.wapitia.sched.parse.node;

import java.util.stream.IntStream;

public class MonthlySchedNode extends SchedNodeContainerBase
    implements SchedNode
{

    public MonthlySchedNode(SchedNode... nodes) {
        super("Monthly", nodes);
    }

    public IntStream daysOfMonthStream() {

        final IntStream result = nodeStream()
            .filter((SchedNode snode) -> snode instanceof DomSpecsNode)
            .map((SchedNode snode) -> (DomSpecsNode) snode)
            .flatMapToInt(DomSpecsNode::daysOfMonthStream);
        return result;
    }
}
