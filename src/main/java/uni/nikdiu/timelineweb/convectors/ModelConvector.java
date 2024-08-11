package uni.nikdiu.timelineweb.convectors;

import org.springframework.stereotype.Component;
import uni.nikdiu.timelineweb.dtos.*;
import uni.nikdiu.timelineweb.entities.Model;
import uni.nikdiu.timelineweb.entities.Parameter;
import uni.nikdiu.timelineweb.services.ParameterService;

import java.util.List;

@Component
public class ModelConvector {
    public FullModelDto toFullDto(Model model) {
        List<ParameterDto> parameters = model.getParameters().stream()
                .map(parameter -> new ParameterConvector().toDto(parameter)).toList();
        return new FullModelDto(
                model.getId(),
                model.getName(),
                model.getDescription(),
                parameters);
    }

    public ModelDto toDto(Model model) {
        return new ModelDto(
                model.getId(),
                model.getName(),
                model.getDescription()
        );
    }

    public Model toEntity(FullModelDto modelDto, ParameterService parameterService) {
        List<Parameter> parameters = modelDto.getParameters().stream()
                .map(parameterDto -> new ParameterConvector().toEntity(parameterDto, parameterService)).toList();

        return new Model(
                modelDto.getId(),
                modelDto.getName(),
                modelDto.getDescription(),
                parameters
        );
    }
}
