package com.grassminevn.levels.jskills.trueskill.layers;

import com.grassminevn.levels.jskills.factorgraphs.Factor;
import com.grassminevn.levels.jskills.factorgraphs.Schedule;
import com.grassminevn.levels.jskills.factorgraphs.ScheduleLoop;
import com.grassminevn.levels.jskills.factorgraphs.ScheduleSequence;
import com.grassminevn.levels.jskills.numerics.GaussianDistribution;
import com.grassminevn.levels.jskills.trueskill.TrueSkillFactorGraph;
import com.grassminevn.levels.jskills.factorgraphs.ScheduleStep;
import com.grassminevn.levels.jskills.factorgraphs.Variable;
import com.grassminevn.levels.jskills.trueskill.factors.GaussianWeightedSumFactor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

// The whole purpose of this is to do a loop on the bottom
public class IteratedTeamDifferencesInnerLayer extends TrueSkillFactorGraphLayer<Variable<GaussianDistribution>, GaussianWeightedSumFactor, Variable<GaussianDistribution>> {

    private final TeamDifferencesComparisonLayer teamDifferencesComparisonLayer;

    private final TeamPerformancesToTeamPerformanceDifferencesLayer teamPerformancesToTeamPerformanceDifferencesLayer;

    public IteratedTeamDifferencesInnerLayer(final TrueSkillFactorGraph parentGraph,
                                             final TeamPerformancesToTeamPerformanceDifferencesLayer teamPerformancesToPerformanceDifferences,
                                             final TeamDifferencesComparisonLayer teamDifferencesComparisonLayer) {
        super(parentGraph);
        teamPerformancesToTeamPerformanceDifferencesLayer = teamPerformancesToPerformanceDifferences;
        this.teamDifferencesComparisonLayer = teamDifferencesComparisonLayer;
    }

    @Override
    public Collection<Factor<GaussianDistribution>> getUntypedFactors() {

        return new ArrayList<Factor<GaussianDistribution>>() {
            private static final long serialVersionUID = 6370771040490033445L; {
           addAll(teamPerformancesToTeamPerformanceDifferencesLayer.getUntypedFactors());
           addAll(teamDifferencesComparisonLayer.getUntypedFactors());
        }};
    }

    @Override
    public void buildLayer() {
        teamPerformancesToTeamPerformanceDifferencesLayer.setRawInputVariablesGroups(getInputVariablesGroups());
        teamPerformancesToTeamPerformanceDifferencesLayer.buildLayer();

        teamDifferencesComparisonLayer.setRawInputVariablesGroups(
                teamPerformancesToTeamPerformanceDifferencesLayer.getRawOutputVariablesGroups());
        teamDifferencesComparisonLayer.buildLayer();
    }

    @Override
    public Schedule<GaussianDistribution> createPriorSchedule() {
        final Schedule<GaussianDistribution> loop;

        switch (getInputVariablesGroups().size()) {
            case 0:
            case 1:
                throw new IllegalArgumentException();
            case 2:
                loop = createTwoTeamInnerPriorLoopSchedule();
                break;
            default:
                loop = createMultipleTeamInnerPriorLoopSchedule();
                break;
        }

        // When dealing with differences, there are always (n-1) differences, so add in the 1
        final int totalTeamDifferences = teamPerformancesToTeamPerformanceDifferencesLayer.getLocalFactors().size();

        final Collection<Schedule<GaussianDistribution>> schedules = new ArrayList<>();
        schedules.add(loop);
        schedules.add(new ScheduleStep<>(
                "teamPerformanceToPerformanceDifferenceFactors[0] @ 1",
                teamPerformancesToTeamPerformanceDifferencesLayer.getLocalFactors().get(0), 1));
        schedules.add(new ScheduleStep<>(
                String.format("teamPerformanceToPerformanceDifferenceFactors[teamTeamDifferences = %d - 1] @ 2",
                        totalTeamDifferences),
                teamPerformancesToTeamPerformanceDifferencesLayer.getLocalFactors().get(totalTeamDifferences - 1), 2));

        return new ScheduleSequence<>(
                "inner schedule", schedules);
    }

    private Schedule<GaussianDistribution> createTwoTeamInnerPriorLoopSchedule() {
        final Collection<Schedule<GaussianDistribution>> schedules = new ArrayList<>();
        schedules.add(new ScheduleStep<>(
                "send team perf to perf differences",
                teamPerformancesToTeamPerformanceDifferencesLayer.getLocalFactors().get(0),
                0));
        schedules.add(new ScheduleStep<>(
                "send to greater than or within factor",
                teamDifferencesComparisonLayer.getLocalFactors().get(0),
                0));
        return ScheduleSequence(schedules,"loop of just two teams inner sequence");
    }

    private Schedule<GaussianDistribution> createMultipleTeamInnerPriorLoopSchedule() {
        final int totalTeamDifferences = teamPerformancesToTeamPerformanceDifferencesLayer.getLocalFactors().size();

        final List<Schedule<GaussianDistribution>> forwardScheduleList = new ArrayList<>();

        for (int i = 0; i < totalTeamDifferences - 1; i++) {
            final Collection<Schedule<GaussianDistribution>> schedules = new ArrayList<>();
            schedules.add(new ScheduleStep<>(
                    String.format("team perf to perf diff %d",
                            i),
                    teamPerformancesToTeamPerformanceDifferencesLayer.getLocalFactors().get(i), 0));
            schedules.add(new ScheduleStep<>(
                    String.format("greater than or within result factor %d",
                            i),
                    teamDifferencesComparisonLayer.getLocalFactors().get(i),
                    0));
            schedules.add(new ScheduleStep<>(
                    String.format("team perf to perf diff factors [%d], 2",
                            i),
                    teamPerformancesToTeamPerformanceDifferencesLayer.getLocalFactors().get(i), 2));
            final Schedule<GaussianDistribution> currentForwardSchedulePiece =
                ScheduleSequence(schedules, "current forward schedule piece %d", i);

            forwardScheduleList.add(currentForwardSchedulePiece);
        }

        final ScheduleSequence<GaussianDistribution> forwardSchedule = new ScheduleSequence<>(
                "forward schedule",
                forwardScheduleList);

        final List<Schedule<GaussianDistribution>> backwardScheduleList = new ArrayList<>();

        for (int i = 0; i < totalTeamDifferences - 1; i++) {
            final Collection<Schedule<GaussianDistribution>> schedules = new ArrayList<>();
            schedules.add(new ScheduleStep<>(
                    String.format("teamPerformanceToPerformanceDifferenceFactors[totalTeamDifferences - 1 - %d] @ 0",
                            i),
                    teamPerformancesToTeamPerformanceDifferencesLayer.getLocalFactors().get(
                            totalTeamDifferences - 1 - i), 0));
            schedules.add(new ScheduleStep<>(
                    String.format("greaterThanOrWithinResultFactors[totalTeamDifferences - 1 - %d] @ 0",
                            i),
                    teamDifferencesComparisonLayer.getLocalFactors().get(totalTeamDifferences - 1 - i), 0));
            schedules.add(new ScheduleStep<>(
                    String.format("teamPerformanceToPerformanceDifferenceFactors[totalTeamDifferences - 1 - %d] @ 1",
                            i),
                    teamPerformancesToTeamPerformanceDifferencesLayer.getLocalFactors().get(
                            totalTeamDifferences - 1 - i), 1));
            
            final ScheduleSequence<GaussianDistribution> currentBackwardSchedulePiece = new ScheduleSequence<>(
                    "current backward schedule piece", schedules);
            backwardScheduleList.add(currentBackwardSchedulePiece);
        }

        final ScheduleSequence<GaussianDistribution> backwardSchedule = new ScheduleSequence<>(
                "backward schedule",
                backwardScheduleList);

        final Collection<Schedule<GaussianDistribution>> schedules = new ArrayList<>();
        schedules.add(forwardSchedule);
        schedules.add(backwardSchedule);
        final ScheduleSequence<GaussianDistribution> forwardBackwardScheduleToLoop = new ScheduleSequence<>(
                "forward Backward Schedule To Loop", schedules);

        final double initialMaxDelta = 0.0001;

        return new ScheduleLoop<>(String.format("loop with max delta of %f", initialMaxDelta),
                                  forwardBackwardScheduleToLoop, initialMaxDelta);
    }
}