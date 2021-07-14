package mca.api.types;

import lombok.AllArgsConstructor;
import lombok.Getter;
import mca.enums.Constraint;

import java.util.Map;

/**
 * APIButton is a button defined in assets/mca/api/gui/*
 * <p>
 * These buttons are dynamically attached to a Screen and include additional instruction/constraints for building
 * and processing interactions.
 */
@AllArgsConstructor
public class APIButton {
    @Getter
    private final int id;             // numeric id
    @Getter
    private final String identifier;  // string identifier for the button in the .lang file
    @Getter
    private final int x;              // x position
    @Getter
    private final int y;              // y position
    @Getter
    private final int width;          // button width
    @Getter
    private final int height;         // button height
    @Getter
    private final boolean notifyServer;   // whether the button press is sent to the server for processing
    @Getter
    private final boolean targetServer;   // whether the button is processed by the villager or the server itself
    private final String constraints;     // list of EnumConstraints separated by a comma
    @Getter
    private final boolean isInteraction;  // whether the button is an interaction that generates a response and boosts/decreases hearts
    @Getter
    private final boolean hideOnFail;  // whether the button is an interaction that generates a response and boosts/decreases hearts
    @Getter
    private Map<Constraint, Boolean> constraintMap;  // cached constraints

    public Map<Constraint, Boolean> getConstraints() {
        if (constraintMap == null) {
            constraintMap = Constraint.fromStringList(constraints);
        }
        return constraintMap;
    }

    //checks if a map of given evaluated constraints apply to this button
    public boolean isValidForConstraint(Map<String, Boolean> checkedConstraints) {
        Map<Constraint, Boolean> constraints = getConstraints();
        for (Map.Entry<Constraint, Boolean> constraint : constraints.entrySet()) {
            if (checkedConstraints.get(constraint.getKey().getId()).equals(constraint.getValue())) {
                return false;
            }
        }
        return true;
    }
}
