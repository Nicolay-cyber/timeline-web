package uni.nikdiu.timelineweb.convectors;


import org.springframework.stereotype.Component;
import uni.nikdiu.timelineweb.dtos.ParameterDto;
import uni.nikdiu.timelineweb.entities.Parameter;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ParameterConvector {
    public ParameterDto toDto(Parameter parameter){
        List<String> functionExpression = parameter.getFunctions().stream()
                .map(function -> function.getStringExpression())
                .collect(Collectors.toList());

        return new ParameterDto(parameter.getId(), parameter.getName(), parameter.getDescription(), parameter.getAbbreviation(), functionExpression);
    }

}
