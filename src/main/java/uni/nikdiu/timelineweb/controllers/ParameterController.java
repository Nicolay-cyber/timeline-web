package uni.nikdiu.timelineweb.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uni.nikdiu.timelineweb.convectors.ExpressionTypeConverter;
import uni.nikdiu.timelineweb.convectors.FunctionConvector;
import uni.nikdiu.timelineweb.convectors.MathNotationConvector;
import uni.nikdiu.timelineweb.convectors.ParameterConvector;
import uni.nikdiu.timelineweb.dtos.FunctionDto;
import uni.nikdiu.timelineweb.dtos.ParameterDto;
import uni.nikdiu.timelineweb.entities.Function;
import uni.nikdiu.timelineweb.entities.Parameter;
import uni.nikdiu.timelineweb.services.FunctionService;
import uni.nikdiu.timelineweb.services.ParameterService;
import uni.nikdiu.timelineweb.services.UnitService;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/parameters")
@RequiredArgsConstructor
public class ParameterController {
    private final ParameterService parameterService;
    private final FunctionService functionService;

    private final ParameterConvector parameterConvector;
    private final FunctionConvector functionConvector;
    private final UnitService unitService;

    @GetMapping
    public List<ParameterDto> getAllParameters() {
        System.out.println("Received the request for all parameters");
        List<ParameterDto> parameters = parameterService.getAllParameters()
                .stream()
                .map(parameter -> parameterConvector.toDto(parameter))
                .collect(Collectors.toList());
        System.out.println("Sent parameters:");
        parameters.forEach(System.out::println);
        return parameters;
    }

    // В вашем контроллере ParameterController
    @GetMapping("/{id}/dependent-functions")
    public ResponseEntity<List<FunctionDto>> getDependentFunctions(@PathVariable Long id) {
        Parameter parameter = parameterService.getParameterById(id);
        System.out.println("Received request to delete parameter: " + parameter);
        List<Function> dependentFunctions = functionService.getAllFunctionsWithParameter(parameter);
        List<FunctionDto> functionDtoList = new ArrayList<>();
        dependentFunctions.forEach(f -> functionDtoList.add(functionConvector.toDto(f)));
        System.out.println("Sent dependent functions for conformation: " + functionDtoList);
        return ResponseEntity.ok(functionDtoList);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteParameter(@PathVariable Long id) {
        parameterService.deleteParameter(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<ParameterDto> editParameter(@PathVariable Long id, @RequestBody ParameterDto parameterDto) {
        System.out.println("Received request to update parameterDto: " + parameterDto);

        Map<FunctionDto, List<Parameter>> relatedParameters = parameterDto.getFunctions().stream().collect(Collectors.toMap(f ->
                        f,
                f -> {
                    return findAllRelatedParameters(f);
                }
        ));

        Parameter parameter = parameterConvector.toEntity(parameterDto, relatedParameters);
        System.out.println("intermediate: "+ parameter);
        parameter = parameterService.updateParameter(id, parameter);

        System.out.println("Parameter successfully updated: ");
        System.out.println(parameter);
        return ResponseEntity.ok(parameterConvector.toDto(parameter));
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

    @PostMapping()
    public ParameterDto addParameter(@RequestBody ParameterDto parameterDto) {

        System.out.println("Received request to save parameter: " + parameterDto);

        Map<FunctionDto, List<Parameter>> relatedParameters = parameterDto.getFunctions().stream().collect(Collectors.toMap(f ->
                        f,
                f -> {
                    return findAllRelatedParameters(f);
                }
        ));

        Parameter parameter = parameterConvector.toEntity(parameterDto, relatedParameters);

        parameter = parameterService.save(parameter);

        System.out.println("New parameter is saved:\n" + parameter);
        return parameterConvector.toDto(parameter);
    }
}
