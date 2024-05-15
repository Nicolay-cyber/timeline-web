package uni.nikdiu.timelineweb.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uni.nikdiu.timelineweb.entities.Function;
import uni.nikdiu.timelineweb.entities.Parameter;
import uni.nikdiu.timelineweb.models.Calculator;
import uni.nikdiu.timelineweb.models.Line;
import uni.nikdiu.timelineweb.models.Point;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GraphService {
    private final ParameterService parameterService;

    public Line getLineByParameterId(Long id) {
        List<Function> relatedFunctions = new ArrayList<>();
        //Find the target parameter
        Parameter targetParameter = parameterService.getParameterById(id);
        targetParameter.getFunctions().forEach(f -> collectRelatedFunctions(f, relatedFunctions, f.getStartPoint(), f.getEndPoint()));

        //Set the step
        double step = 100;
        List<Point> points = new ArrayList<>();
        Calculator calculator = new Calculator(parameterService);
        targetParameter.getFunctions().forEach(function -> {
            for (Double i = function.getStartPoint(); i <= function.getEndPoint(); i += step) {

                Point point = new Point(i, calculator.calculate(
                        Arrays.asList(function.getStringExpression().split(" ")),
                        relatedFunctions,
                        i
                ));
                points.add(point);
            }
        });
        return new Line(points);
    }
    private void collectRelatedFunctions(Function targetFunction, List<Function> relatedFunctions, Double startPoint, Double endPoint) {
        if (!relatedFunctions.contains(targetFunction)) {
            relatedFunctions.add(targetFunction);
            targetFunction.getRelatedParameters().forEach(parameter -> {
                Parameter relatedParameter = parameterService.getParameterById(parameter.getId());
                relatedParameter.getFunctions().forEach(f -> {
                    //Collect all related functions which start and end points within target function start and end points
                    if (f.getStartPoint() >= startPoint && f.getStartPoint() <= endPoint || f.getEndPoint() >= startPoint && f.getEndPoint() <= endPoint) {
                        collectRelatedFunctions(f, relatedFunctions, startPoint, endPoint);
                    }
                });
            });
        }
    }

}
