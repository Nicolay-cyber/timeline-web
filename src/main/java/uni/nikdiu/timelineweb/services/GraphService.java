package uni.nikdiu.timelineweb.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uni.nikdiu.timelineweb.dtos.*;
import uni.nikdiu.timelineweb.entities.Function;
import uni.nikdiu.timelineweb.entities.Model;
import uni.nikdiu.timelineweb.entities.Parameter;
import uni.nikdiu.timelineweb.entities.Point;
import uni.nikdiu.timelineweb.models.Calculator;
import uni.nikdiu.timelineweb.models.Line;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GraphService {
    private final ParameterService parameterService;

    public ModelGraphDto getModelGraph(Model model) {
        ModelGraphDto modelGraph = new ModelGraphDto();
        modelGraph.setXValues(getXValues(model));


        Calculator calculator = new Calculator();
        List<Function> relatedFunctions = new ArrayList<>();
        List<Parameter> relatedParameters = new ArrayList<>();
        List<LineDto> YValuesLines = new ArrayList<>();
        model.getParameters().forEach(parameter -> {
            if (!parameter.getPoints().isEmpty() && parameter.getPoints().size() > 1) {
                addNewCreatedFunctionsFromParameterPoints(parameter);
            }
            List<Function> functionsCopy = new ArrayList<>(parameter.getFunctions());

            functionsCopy.forEach(f -> {
                collectRelatedFunctionsAndParameters(f, relatedFunctions, relatedParameters, f.getStartPoint(), f.getEndPoint());
            });

            List<Double> yValues = new ArrayList<>();
            functionsCopy.forEach(function -> {
                modelGraph.getXValues().forEach(x -> {
                            if (x >= function.getStartPoint() && x <= function.getEndPoint()) {
                                Double y = calculator.calculate(
                                        Arrays.asList(function.getStringExpression().split(" ")),
                                        relatedFunctions,
                                        relatedParameters,
                                        x
                                );
                                if (y != null && isValidNumber(y)) {
                                    yValues.add(y);
                                }
                            }

                        }
                );
            });

            List<String> XValuesString = modelGraph.getXValues().stream()
                    .map(String::valueOf)
                    .collect(Collectors.toList());

            YValuesLines.add(
                    new LineDto(
                            yValues,
                            XValuesString,
                            parameter.getName()
                    )
            );

        });

        modelGraph.setYValues(YValuesLines);
        return modelGraph;
    }

    private List<Double> getXValues(Model model) {
        double smallestStartPoint = model.getParameters().stream()
                .flatMap(parameter -> parameter.getFunctions().stream()
                        .map(Function::getStartPoint)
                        .filter(startPoint -> !Double.isNaN(startPoint)))
                .min(Double::compare)
                .orElse(Double.NaN);

        double smallestXPoint = model.getParameters().stream()
                .flatMap(parameter -> parameter.getPoints().stream()
                        .map(Point::getX)
                        .filter(x -> !Double.isNaN(x)))
                .min(Double::compare)
                .orElse(Double.NaN);

        double overallSmallestPoint = Double.isNaN(smallestStartPoint) ? smallestXPoint :
                (Double.isNaN(smallestXPoint) ? smallestStartPoint :
                        Math.min(smallestStartPoint, smallestXPoint));

        double biggestEndPoint = model.getParameters().stream()
                .flatMap(parameter -> parameter.getFunctions().stream()
                        .map(Function::getEndPoint)
                        .filter(endPoint -> !Double.isNaN(endPoint)))
                .max(Double::compare)
                .orElse(Double.NaN);

        double biggestXPoint = model.getParameters().stream()
                .flatMap(parameter -> parameter.getPoints().stream()
                        .map(Point::getX)
                        .filter(x -> !Double.isNaN(x)))
                .max(Double::compare)
                .orElse(Double.NaN);

        double overallBiggestPoint = Double.isNaN(biggestEndPoint) ? biggestXPoint :
                (Double.isNaN(biggestXPoint) ? biggestEndPoint :
                        Math.max(biggestEndPoint, biggestXPoint));

        List<Double> XValues = new ArrayList<>();
        if (!Double.isNaN(overallSmallestPoint) && !Double.isNaN(overallBiggestPoint)) {
            int pointAmount = 50;
            Double step = (overallBiggestPoint - overallSmallestPoint) / pointAmount;
            double currentValue = smallestStartPoint;
            for (int i = 0; i < pointAmount; i++) {
                XValues.add(currentValue);
                currentValue += step;
            }
            if (!XValues.contains(overallBiggestPoint)) {
                XValues.add(overallBiggestPoint);
            }
            System.out.println(XValues);
        } else {
            System.out.println("Failed to calculate step due to NaN values");
        }
        return XValues;
    }

    public Line getLineByParameterId(Long id) {

        List<Function> relatedFunctions = new ArrayList<>();
        List<Parameter> relatedParameters = new ArrayList<>();
        //Find the target parameter
        Parameter targetParameter = parameterService.getParameterById(id);
        if (!targetParameter.getPoints().isEmpty() && targetParameter.getPoints().size() > 1) {
            addNewCreatedFunctionsFromParameterPoints(targetParameter);
        }

        List<Function> functionsCopy = new ArrayList<>(targetParameter.getFunctions());

        functionsCopy.forEach(f -> {
            collectRelatedFunctionsAndParameters(f, relatedFunctions, relatedParameters, f.getStartPoint(), f.getEndPoint());
        });

        Optional<Double> smallestStartPoint = functionsCopy.stream()
                .map(Function::getStartPoint)
                .min(Double::compareTo);

        Optional<Double> biggestEndPoint = functionsCopy.stream()
                .map(Function::getEndPoint)
                .max(Double::compareTo);

        double startPoint = smallestStartPoint.orElse(0.0);
        double endPoint = biggestEndPoint.orElse(0.0);
        double step = (endPoint - startPoint) / 50;
        List<Double> points = new ArrayList<>();
        List<Double> labels = new ArrayList<>();
        Calculator calculator = new Calculator();

        functionsCopy.forEach(function -> {
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

        Line line = new Line(targetParameter.getName(), points, labels);
        System.out.println("Sent line:\n" + line);
        return line;
    }

    public void addNewCreatedFunctionsFromParameterPoints(Parameter targetParameter) {
        List<Point> parameterPoints = new ArrayList<>(targetParameter.getPoints());

        for (int i = 0; i < parameterPoints.size() - 1; i++) {
            Point endPoint = parameterPoints.get(i);
            Point startPoint = parameterPoints.get(i + 1);

            double slope = (endPoint.getY() - startPoint.getY()) / (endPoint.getX() - startPoint.getX());
            double intercept = startPoint.getY() - slope * startPoint.getX();

            String stringExpression = slope + " * @time + " + intercept;
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
            List<Parameter> targetFunctionRelatedParameters = new ArrayList<>(targetFunction.getRelatedParameters());
            for (Parameter parameter : targetFunctionRelatedParameters) {
                Parameter relatedParameter = parameterService.getParameterById(parameter.getId());
                if (!relatedParameter.getPoints().isEmpty() && relatedParameter.getPoints().size() > 1) {
                    addNewCreatedFunctionsFromParameterPoints(relatedParameter);
                }
                if (!relatedParameters.contains(relatedParameter)) {
                    relatedParameters.add(relatedParameter);
                }
                List<Function> relatedParameterFunctions = new ArrayList<>(relatedParameter.getFunctions());
                for (Function f : relatedParameterFunctions) {
                    if (!(endPoint < f.getStartPoint() || startPoint > f.getEndPoint())) {
                        collectRelatedFunctionsAndParameters(f, relatedFunctions, relatedParameters, startPoint, endPoint);
                    }
                }
            }
        }
    }

}
