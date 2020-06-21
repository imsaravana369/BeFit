package com.saravana.befit.Manual;

import com.saravana.befit.model.Exercise;

public interface ManualCardViewInterface {
    boolean  incrementExerciseCount(Exercise exercise);
    boolean decreaseExerciseCount(Exercise exercise);
    boolean increaseMinutes();
    boolean decreaseMinutes();
}
