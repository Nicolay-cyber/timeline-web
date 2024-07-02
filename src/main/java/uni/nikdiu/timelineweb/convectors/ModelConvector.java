package uni.nikdiu.timelineweb.convectors;

import org.springframework.stereotype.Component;
import uni.nikdiu.timelineweb.dtos.*;
import uni.nikdiu.timelineweb.entities.Model;
import uni.nikdiu.timelineweb.entities.Parameter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ModelConvector {
    public FullModelDto toFullDto(Model model) {
        List<ParameterDto> parameters = model.getParameters().stream()
                .map(parameter -> new ParameterConvector().toDto(parameter)).toList();
        List<Double> XValues = List.of(100.0, 200.0, 300.0, 400.0, 500.0, 600.0, 700.0, 800.0, 900.0, 1000.0);
        List<Double> YValues1 = List.of(860.0, 1140.0, 1060.0, 1060.0, 1070.0, 1110.0, 1330.0, 2210.0, 7830.0, 2478.0);
        List<Double> YValues2 = List.of(1600.0, 1700.0, 1700.0, 1900.0, 2000.0, 2700.0, 4000.0, 5000.0, 6000.0, 7000.0);
        List<Double> YValues3 = List.of(300.0, 700.0, 2000.0, 5000.0, 6000.0, 4000.0, 2000.0, 1000.0, 200.0, 100.0);

        List<String> XValuesString = XValues.stream()
                .map(String::valueOf)
                .collect(Collectors.toList());
        List<LineDto> YValues = List.of(
                new LineDto(
                        YValues1,
                        XValuesString,
                        "Red"
                ),
                new LineDto(
                        YValues2,
                        XValuesString,
                        "Blue"
                ),
                new LineDto(
                        YValues3,
                        XValuesString,
                        "Yellow"
                )

        );
        return new FullModelDto(
                model.getId(),
                model.getName(),
                model.getDescription(),
                parameters,
                XValues,
                YValues
        );
    }

    public ModelDto toDto(Model model) {
        return new ModelDto(
                model.getId(),
                model.getName(),
                model.getDescription()
        );
    }

    public Model toEntity(FullModelDto modelDto) {
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
