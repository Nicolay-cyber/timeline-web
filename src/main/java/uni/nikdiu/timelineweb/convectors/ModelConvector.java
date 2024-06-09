package uni.nikdiu.timelineweb.convectors;

import org.springframework.stereotype.Component;
import uni.nikdiu.timelineweb.dtos.ModelDto;
import uni.nikdiu.timelineweb.dtos.ParameterDto;
import uni.nikdiu.timelineweb.entities.Model;
import uni.nikdiu.timelineweb.entities.Parameter;

import java.util.List;

@Component
public class ModelConvector {

    public ModelDto toDto(Model model){
        List<ParameterDto> parameters = model.getParameters().stream()
                .map(parameter -> new ParameterConvector().toDto(parameter)).toList();
        return new ModelDto(
                model.getId(),
                model.getName(),
                model.getDescription(),
                parameters
        );
    }

    public Model toEntity(ModelDto modelDto){
        List<Parameter> parameters = modelDto.getParameters().stream()
                .map(parameterDto -> new ParameterConvector().toEntity(parameterDto, null)).toList();
        return new Model(
                modelDto.getId(),
                modelDto.getName(),
                modelDto.getDescription(),
                parameters
        );
    }
}
