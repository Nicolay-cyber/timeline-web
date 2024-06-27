package uni.nikdiu.timelineweb.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uni.nikdiu.timelineweb.entities.Function;
import uni.nikdiu.timelineweb.entities.Model;
import uni.nikdiu.timelineweb.entities.Parameter;
import uni.nikdiu.timelineweb.repositories.ParameterRepository;

import java.util.Collection;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ParameterService {
    private final ParameterRepository parameterRepository;
    private final ModelService modelService;
    private final FunctionService functionService;

    public List<Parameter> getAllParameters() {
        return parameterRepository.findAll();
    }

    public Parameter getParameterById(Long id) {
        return parameterRepository.findById(id).orElseThrow(() -> new RuntimeException("Parameter not found"));
    }

    public Parameter getParameterByTag(String tag) {
        return parameterRepository.getParameterByTag(tag).orElseThrow(() -> new RuntimeException("Parameter " + tag + " not found"));
    }

    public Parameter save(Parameter parameter) {
        return parameterRepository.save(parameter);
    }

    @Transactional
    public void deleteParameter(Long id) {
        Parameter parameter = getParameterById(id);
        List<Model> models = modelService.getAllModelsWithParameter(parameter).stream().map(object -> (Model) object).toList();
        //remove the parameter from all models
        for (Model model : models) {
            model.getParameters().remove(parameter);
        }
        //update the edited models
        for (Model model : models) {
            modelService.save(model);
        }
        //collect all functions which depend on this parameter
        List<Function> functions = functionService.getAllFunctionsWithParameter(parameter);

        //remove collected functions
        for (Function function : functions) {
            functionService.remove(function);
        }
        parameterRepository.delete(parameter);
        System.out.println("Parameter deleted");
    }

    public Parameter updateParameter(Long id, Parameter parameterDetails) {
        System.out.println("Entity for updating:\n" + parameterDetails);

        Parameter parameter = getParameterById(id);

        parameter.setName(parameterDetails.getName());
        parameter.setAbbreviation(parameterDetails.getAbbreviation());
        parameter.setDescription(parameterDetails.getDescription());
        parameter.setUnit(parameterDetails.getUnit());

        parameterDetails.getFunctions().forEach(function -> {
            if (function.getId() == null) {
                functionService.save(function);
            } else {
                functionService.update(function);
            }
        });

        return parameterRepository.save(parameter);
    }
}
