
package com.wapitia.sched.parse.node;

import com.wapitia.sched.CycDow;

import java.util.stream.Stream;

public class BiweeklySchedNode extends SchedNodeContainerBase
    implements SchedNode
{

    public BiweeklySchedNode(SchedNode... nodes) {
        super("Biweekly", nodes);
    }

    public Stream<CycDow> cycledDaysOfWeekStream() {

        final Stream<CycDow> result = nodeStream()
            .filter((SchedNode snode) -> snode instanceof DowomSpecsNode)
            .map((SchedNode snode) -> (DowomSpecsNode) snode)
            .flatMap(DowomSpecsNode::cycledDaysOfWeekStream);
        return result;
    }
}
