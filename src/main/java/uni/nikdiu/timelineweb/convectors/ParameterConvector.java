package uni.nikdiu.timelineweb.convectors;


import org.springframework.stereotype.Component;
import uni.nikdiu.timelineweb.dtos.FunctionDto;
import uni.nikdiu.timelineweb.dtos.ParameterDto;
import uni.nikdiu.timelineweb.entities.Parameter;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ParameterConvector {
    public ParameterDto toDto(Parameter parameter){
        FunctionConvector functionConvector = new FunctionConvector();
        List<FunctionDto> functions = parameter.getFunctions().stream()
                .map(function -> functionConvector.toDto(function)).toList();

        return new ParameterDto(parameter.getId(), parameter.getName(), parameter.getDescription(), parameter.getAbbreviation(), functions);
    }

}
