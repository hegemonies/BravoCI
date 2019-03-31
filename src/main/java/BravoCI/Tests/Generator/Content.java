package BravoCI.Tests.Generator;

import java.util.List;

public class Content {
    private Config config;
    private List<Step> steps;

    public Config getConfig() {
        return config;
    }

    public List<Step> getSteps() {
        return steps;
    }


    @Override
    public String toString() {
        StringBuilder _steps = new StringBuilder();
        for (Step step : steps) {
            _steps.append(step.toString() + "\n");
        }
        return "Config:\n" + config.toString() + "\nSteps:\n" + _steps.toString();
    }
}
