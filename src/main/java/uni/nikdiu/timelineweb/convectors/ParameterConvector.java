package uni.nikdiu.timelineweb.convectors;


import org.springframework.stereotype.Component;
import uni.nikdiu.timelineweb.dtos.FunctionDto;
import uni.nikdiu.timelineweb.dtos.ParameterDto;
import uni.nikdiu.timelineweb.dtos.PointDto;
import uni.nikdiu.timelineweb.dtos.UnitDto;
import uni.nikdiu.timelineweb.entities.Function;
import uni.nikdiu.timelineweb.entities.Parameter;
import uni.nikdiu.timelineweb.entities.Unit;

import java.util.List;
import java.util.Map;

@Component
public class ParameterConvector {
    FunctionConvector functionConvector;
    PointConvector pointConvector;
    UnitConvector unitConvector;

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
                parameter.getDescription(),
                parameter.getAbbreviation(),
                unitDto,
                functions,
                points);
    }

    public Parameter toEntity(ParameterDto parameterDto, Map<FunctionDto, List<Parameter>> relatedParameters) {

        Unit unit = unitConvector.toEntity(parameterDto.getUnit());


        Parameter parameter = new Parameter(
                parameterDto.getId(),
                parameterDto.getName(),
                parameterDto.getDescription(),
                parameterDto.getAbbreviation());

        functionConvector = new FunctionConvector();

        List<Function> functions = parameterDto.getFunctions().stream()
                .map(f -> functionConvector.toEntity(f, parameter, relatedParameters.get(f))).toList();
        parameter.setFunctions(functions);
        parameter.setUnit(unit);
        return parameter;
    }

}
