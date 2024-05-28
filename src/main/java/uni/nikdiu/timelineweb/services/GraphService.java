package uni.nikdiu.timelineweb.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uni.nikdiu.timelineweb.entities.Function;
import uni.nikdiu.timelineweb.entities.Parameter;
import uni.nikdiu.timelineweb.entities.Point;
import uni.nikdiu.timelineweb.models.Calculator;
import uni.nikdiu.timelineweb.models.Line;

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
        //if the target parameter consists manually added point instead of function,
        // then locally create linear functions to get set of point from the same distance
        // and to use this function for subsequent calculations
        System.out.println(targetParameter);
        if (!targetParameter.getPoints().isEmpty() && targetParameter.getPoints().size() > 1) {
            addNewCreatedFunctionsFromParameterPoints(targetParameter);
        }
        System.out.println(targetParameter);

        //Set the step
        // Find the smallest and biggest start/end points among functions of the target parameter
        Optional<Double> smallestStartPoint = targetParameter.getFunctions().stream()
                .map(Function::getStartPoint)
                .min(Double::compareTo);

        Optional<Double> biggestEndPoint = targetParameter.getFunctions().stream()
                .map(Function::getEndPoint)
                .max(Double::compareTo);

        double startPoint = smallestStartPoint.orElse(0.0);
        double endPoint = biggestEndPoint.orElse(0.0);
        System.out.println(startPoint + " " + endPoint);
        // Set double step for 50 points
        double step = (endPoint - startPoint) / 50;
        List<Double> points = new ArrayList<>();
        List<Double> labels = new ArrayList<>();
        Calculator calculator = new Calculator();




        targetParameter.getFunctions().forEach(function -> {
            for (Double i = function.getStartPoint(); i <= function.getEndPoint(); i += step) {
                Double y = calculator.calculate(
                        Arrays.asList(function.getStringExpression().split(" ")),
                        relatedFunctions,
                        relatedParameters,
                        i
                );
                if (y != null && isValidNumber(y)) {
                    points.add(y);
                    labels.add(i);
                }
            }
        });

        Line line= new Line(targetParameter.getName(), points, labels);
        System.out.println(line);
return line;
    }

    public void addNewCreatedFunctionsFromParameterPoints(Parameter targetParameter) {
        List<Point> parameterPoints = targetParameter.getPoints();

        for (int i = 0; i < targetParameter.getPoints().size() - 1; i++) {
            Point endPoint = parameterPoints.get(i);
            Point startPoint = parameterPoints.get(i + 1);

            double slope = (endPoint.getY() - startPoint.getY()) / (endPoint.getX() - startPoint.getX());
            double intercept = startPoint.getY() - slope * startPoint.getX();

            String stringExpression = slope + " * t + " + intercept;
            Function function = new Function(null,
                    endPoint.getX(),
                    startPoint.getX(),
                    stringExpression,
                    Arrays.asList(stringExpression.split(" ")));
            function.setRelatedParameters(new ArrayList<>());
            function.setParentParameter(targetParameter);
            targetParameter.getFunctions().add(function);
        }
    }

    private boolean isValidNumber(Double number) {
        return !number.isNaN() && !number.isInfinite();
    }

    private void collectRelatedFunctionsAndParameters(
            Function targetFunction,
            List<Function> relatedFunctions,
            List<Parameter> relatedParameters,
            Double startPoint,
            Double endPoint) {

        targetFunction.setExpression(Arrays.asList(targetFunction.getStringExpression().split(" ")));
        if (!relatedFunctions.contains(targetFunction)) {
            relatedFunctions.add(targetFunction);
            targetFunction.getRelatedParameters().forEach(parameter -> {
                Parameter relatedParameter = parameterService.getParameterById(parameter.getId());
                if (!relatedParameter.getPoints().isEmpty() && relatedParameter.getPoints().size() > 1) {
                    addNewCreatedFunctionsFromParameterPoints(relatedParameter);
                }
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
