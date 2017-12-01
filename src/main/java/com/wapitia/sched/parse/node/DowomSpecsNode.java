
package com.wapitia.sched.parse.node;

import com.wapitia.sched.CycDow;
import com.wapitia.sched.WeekOfMonth;

import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public class DowomSpecsNode extends SchedNodeContainerBase
    implements SchedNode
{

    public DowomSpecsNode(SchedNode... nodes) {
        super("DowomSpec", nodes);
    }

    public Stream<CycDow> cycledDaysOfWeekStream() {

        final Optional<WeeksOfMonthNode> womNode = nodeStream()
            .filter((SchedNode snode) -> snode instanceof WeeksOfMonthNode)
            .map((SchedNode snode) -> (WeeksOfMonthNode) snode).findFirst();

        final Optional<DaysOfWeekNode> dowNode = nodeStream()
            .filter((SchedNode snode) -> snode instanceof DaysOfWeekNode)
            .map((SchedNode snode) -> (DaysOfWeekNode) snode).findFirst();

        final List<CycDow> resultList = new ArrayList<>();
        dowNode.ifPresent((DaysOfWeekNode dowN) -> {
            womNode.ifPresent((WeeksOfMonthNode womN) -> {
                dowN.getDayOfWeekSet().forEach((DayOfWeek dow) -> {
                    womN.getWeekOfMonthSet().forEach((WeekOfMonth woc) -> {
                        CycDow cycDow = new CycDow(woc, dow);
                        resultList.add(cycDow);
                    });
                });
            });
        });

        return resultList.stream();
    }
}
