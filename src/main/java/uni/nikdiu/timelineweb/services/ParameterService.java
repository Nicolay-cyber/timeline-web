package uni.nikdiu.timelineweb.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uni.nikdiu.timelineweb.entities.Model;
import uni.nikdiu.timelineweb.entities.Parameter;
import uni.nikdiu.timelineweb.repositories.ParameterRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ParameterService {
    private final ParameterRepository parameterRepository;
    private final ModelService modelService;

    public List<Parameter> getAllParameters() {
        return parameterRepository.findAll();
    }

    public Parameter getParameterById(Long id) {
        return parameterRepository.findById(id).orElseThrow(() -> new RuntimeException("Parameter not found"));
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

        parameterRepository.delete(parameter);
    }

    public Parameter updateParameter(Long id, Parameter parameterDetails) {
        Parameter parameter = getParameterById(id);

        parameter.setName(parameterDetails.getName());
        parameter.setAbbreviation(parameterDetails.getAbbreviation());
        parameter.setDescription(parameterDetails.getDescription());
        parameter.setFunctions(parameterDetails.getFunctions());
        parameter.setUnit(parameterDetails.getUnit());

        return parameterRepository.save(parameter);
    }
}
