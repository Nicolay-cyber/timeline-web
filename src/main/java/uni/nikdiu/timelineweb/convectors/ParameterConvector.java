package uni.nikdiu.timelineweb.convectors;


import org.springframework.stereotype.Component;
import uni.nikdiu.timelineweb.dtos.FunctionDto;
import uni.nikdiu.timelineweb.dtos.ParameterDto;
import uni.nikdiu.timelineweb.dtos.PointDto;
import uni.nikdiu.timelineweb.dtos.UnitDto;
import uni.nikdiu.timelineweb.entities.Function;
import uni.nikdiu.timelineweb.entities.Parameter;
import uni.nikdiu.timelineweb.entities.Point;
import uni.nikdiu.timelineweb.entities.Unit;
import uni.nikdiu.timelineweb.services.ParameterService;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class ParameterConvector {
    FunctionConvector functionConvector;
    PointConvector pointConvector;
    UnitConvector unitConvector;
    ParameterService parameterService;

    public ParameterDto toDto(Parameter parameter) {
        functionConvector = new FunctionConvector();
        pointConvector = new PointConvector();
        unitConvector = new UnitConvector();

        List<FunctionDto> functions = parameter.getFunctions().stream()
                .map(function -> functionConvector.toDto(function)).toList();
        List<PointDto> points = parameter.getPoints().stream()
                .map(point -> pointConvector.toDto(point)).toList();

        UnitDto unitDto = unitConvector.toDto(parameter.getUnit());
        return new ParameterDto(
                parameter.getId(),
                parameter.getName(),
                parameter.getTag(),
                parameter.getDescription(),
                parameter.getAbbreviation(),
                unitDto,
                functions,
                points);
    }

    public Parameter toEntity(ParameterDto parameterDto, ParameterService parameterService) {
        unitConvector = new UnitConvector();
        functionConvector = new FunctionConvector();
        pointConvector = new PointConvector();
        this.parameterService = parameterService;

        Map<FunctionDto, List<Parameter>> relatedParameters = parameterDto.getFunctions().stream().collect(Collectors.toMap(f ->
                        f,
                f -> {
                    return findAllRelatedParameters(f);
                }
        ));

        Unit unit = unitConvector.toEntity(parameterDto.getUnit());


        Parameter parameter = new Parameter(
                parameterDto.getId(),
                parameterDto.getName(),
                parameterDto.getTag(),
                parameterDto.getDescription(),
                parameterDto.getAbbreviation());


        List<Function> functions = parameterDto.getFunctions().stream()
                .map(f -> functionConvector.toEntity(f, parameter, relatedParameters.get(f))).toList();
        parameter.setFunctions(functions);
        parameter.setUnit(unit);
        List<Point> points = parameterDto.getPoints().stream()
                .map(point -> pointConvector.toEntity(point,parameter))
                .sorted(Comparator.comparing(Point::getX))
                .toList();
        parameter.setPoints(points);
        return parameter;
    }
    private List<Parameter> findAllRelatedParameters(FunctionDto f) {
        System.out.println();
        System.out.println("findAllRelatedParameters ");
        ExpressionTypeConverter expressionConvector = new ExpressionTypeConverter();
        String newExpression = expressionConvector.latexToClassic(f.getTagParamExpression());
        List<String> expressionList = Arrays.asList(newExpression.split(" "));
        List<Parameter> relatedParameters = new ArrayList<>();
        System.out.println("f.getTagParamExpression() " + f.getTagParamExpression());
        System.out.println("newExpression " + newExpression);
        System.out.println("expressionList " + expressionList);

        expressionList.forEach(token -> {
            if (token.startsWith("@") && !token.equals("@time")) {
                relatedParameters.add(parameterService.getParameterByTag(token));
            }
        });
        return relatedParameters;
    }
}
