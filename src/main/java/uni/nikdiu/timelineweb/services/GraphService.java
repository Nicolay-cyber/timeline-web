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
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GraphService {
    private final ParameterService parameterService;

    public Line getLineByParameterId(Long id) {

        List<Function> relatedFunctions = new ArrayList<>();
        List<Parameter> relatedParameters = new ArrayList<>();
        //Find the target parameter
        Parameter targetParameter = parameterService.getParameterById(id);
        targetParameter.getFunctions().forEach(f -> {
            collectRelatedFunctionsAndParameters(f, relatedFunctions, relatedParameters, f.getStartPoint(), f.getEndPoint());
        });

        //Set the step
        double step = 100;
        List<Point> points = new ArrayList<>();
        Calculator calculator = new Calculator();

        targetParameter.getFunctions().forEach(function -> {
            for (Double i = function.getStartPoint(); i <= function.getEndPoint(); i += step) {
                Double y =  calculator.calculate(
                        Arrays.asList(function.getStringExpression().split(" ")),
                        relatedFunctions,
                        relatedParameters,
                        i
                );
                if(y != null){
                    Point point = new Point(i,y );
                    points.add(point);
                }
            }
        });
        return new Line(points);
    }

    private void collectRelatedFunctionsAndParameters(
            Function targetFunction,
            List<Function> relatedFunctions,
            List<Parameter> relatedParameters,
            Double startPoint, Double endPoint) {

        targetFunction.setExpression(Arrays.asList(targetFunction.getStringExpression().split(" ")));
        if (!relatedFunctions.contains(targetFunction)) {
            relatedFunctions.add(targetFunction);
            targetFunction.getRelatedParameters().forEach(parameter -> {
                Parameter relatedParameter = parameterService.getParameterById(parameter.getId());
                if (!relatedParameters.contains(relatedParameter)) {
                    relatedParameters.add(relatedParameter);
                }
                relatedParameter.getFunctions().forEach(f -> {
                    //Collect all related functions which start and end points within target function start and end points
                    if (!(endPoint < f.getStartPoint()
                            || startPoint > f.getEndPoint())
                    ) { //checking function is inside of target
                        collectRelatedFunctionsAndParameters(f, relatedFunctions, relatedParameters, startPoint, endPoint);
                    }
                });
            });
        }
    }
}
