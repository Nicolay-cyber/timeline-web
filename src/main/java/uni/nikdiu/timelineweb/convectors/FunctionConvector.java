package uni.nikdiu.timelineweb.convectors;

import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;
import uni.nikdiu.timelineweb.dtos.FunctionDto;
import uni.nikdiu.timelineweb.entities.Function;
import uni.nikdiu.timelineweb.entities.Parameter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Component
@NoArgsConstructor
public class FunctionConvector {
    ExpressionTypeConverter latexConvector = new ExpressionTypeConverter();

    public FunctionDto toDto(Function function) {
        List<Long> relatedParameters = function.getRelatedParameters().stream().map(f -> f.getId()).toList();
        return new FunctionDto(
                function.getId(),
                function.getStartPoint(),
                function.getEndPoint(),
                function.getParentParameter().getId(),
                function.getParentParameter().getName(),
                latexConvector.classicToLatex(function.getStringExpression()),
                relatedParameters);
    }

    public Function toEntity(FunctionDto functionDto,Parameter parentParameter, List<Parameter> relatedParameters) {
        String stringExpression = latexConvector.latexToClassic(functionDto.getExpression());
        List<String> expressions = Arrays.asList(stringExpression.split(" "));
        return new Function(
                functionDto.getId(),
                functionDto.getStartPoint(),
                functionDto.getEndPoint(),
                stringExpression,
                parentParameter,
                relatedParameters,
                expressions
        );
    }
    public Function toEntity(FunctionDto functionDto) {
        List<String> expressions = Arrays.asList(latexConvector.latexToClassic(functionDto.getExpression()).split(" "));
        return new Function(
                functionDto.getId(),
                functionDto.getStartPoint(),
                functionDto.getEndPoint(),
                functionDto.getExpression(),
                expressions
        );
    }
}
