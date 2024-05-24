package uni.nikdiu.timelineweb.convectors;


import org.springframework.stereotype.Component;
import uni.nikdiu.timelineweb.dtos.FunctionDto;
import uni.nikdiu.timelineweb.dtos.ParameterDto;
import uni.nikdiu.timelineweb.entities.Function;
import uni.nikdiu.timelineweb.entities.Parameter;

import java.util.List;
import java.util.Map;

@Component
public class ParameterConvector {
    FunctionConvector functionConvector;

    public ParameterDto toDto(Parameter parameter) {
        functionConvector = new FunctionConvector();
        List<FunctionDto> functions = parameter.getFunctions().stream()
                .map(function -> functionConvector.toDto(function)).toList();

        return new ParameterDto(parameter.getId(), parameter.getName(), parameter.getDescription(), parameter.getAbbreviation(), functions);
    }

    public Parameter toEntity(ParameterDto parameterDto, Map<FunctionDto, List<Parameter>> relatedParameters) {
        Parameter parameter = new Parameter(
                parameterDto.getId(),
                parameterDto.getName(),
                parameterDto.getDescription(),
                parameterDto.getAbbreviation());

        functionConvector = new FunctionConvector();

        List<Function> functions = parameterDto.getFunctions().stream()
                .map(f -> functionConvector.toEntity(f, parameter, relatedParameters.get(f))).toList();
        parameter.setFunctions(functions);
        return parameter;
    }

}
