package uni.nikdiu.timelineweb.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import uni.nikdiu.timelineweb.convectors.ParameterConvector;
import uni.nikdiu.timelineweb.dtos.ParameterDto;
import uni.nikdiu.timelineweb.services.ParameterService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/parameters")
@RequiredArgsConstructor
public class ParameterController {
    private final ParameterService parameterService;
    private final ParameterConvector parameterConvector;

    @GetMapping
    public List<ParameterDto> getAllParameters() {
        return parameterService.getAllParameters()
                .stream()
                .map(parameter -> parameterConvector.toDto(parameter))
                .collect(Collectors.toList());
    }

    @PostMapping()
    public void receiveFormula(@RequestBody FormulaRequest formulaRequest) {
        System.out.println("Received formula: " + formulaRequest.getFormula());
    }
    public static class FormulaRequest {
        private String formula;

        public String getFormula() {
            return formula;
        }

        public void setFormula(String formula) {
            this.formula = formula;
        }
    }
}
