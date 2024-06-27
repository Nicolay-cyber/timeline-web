package uni.nikdiu.timelineweb.convectors;

import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;
import uni.nikdiu.timelineweb.dtos.FunctionDto;
import uni.nikdiu.timelineweb.entities.Function;
import uni.nikdiu.timelineweb.entities.Parameter;

import java.util.Arrays;
import java.util.List;

@Component
@NoArgsConstructor
public class FunctionConvector {
    ExpressionTypeConverter latexConvector = new ExpressionTypeConverter();

    public FunctionDto toDto(Function function) {
        List<Long> relatedParameters = function.getRelatedParameters().stream().map(f -> f.getId()).toList();
        String tagExpression = latexConvector.classicToLatex(function.getStringExpression());
        String abbrExpression = replaceTagsWithAbbreviation(tagExpression, function.getRelatedParameters());

        return new FunctionDto(
                function.getId(),
                function.getStartPoint(),
                function.getEndPoint(),
                function.getParentParameter().getId(),
                function.getParentParameter().getName(),
                abbrExpression,
                tagExpression,
                relatedParameters);
    }

    private String replaceTagsWithAbbreviation(String tagExpression, List<Parameter> relatedParameters) {
        for (Parameter param : relatedParameters) {
            String tag = param.getTag();
            String abbreviation = param.getAbbreviation();
            if (tag != null && abbreviation != null) {
                tagExpression = tagExpression.replace(tag, abbreviation);
            }
        }
        tagExpression = tagExpression.replace("@time", "t");
        return tagExpression;
    }

    public Function toEntity(FunctionDto functionDto, Parameter parentParameter, List<Parameter> relatedParameters) {
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

}
