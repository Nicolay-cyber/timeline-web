package uni.nikdiu.timelineweb.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uni.nikdiu.timelineweb.entities.Model;
import uni.nikdiu.timelineweb.entities.Parameter;
import uni.nikdiu.timelineweb.repositories.ModelRepository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ModelService {
    private final ModelRepository modelRepository;

    public List<Model> getAllModels() {
        return modelRepository.findAll();
    }

    public Model getModelById(Long id) {
        return modelRepository.findById(id).orElseThrow(() -> new RuntimeException("Model not found"));
    }

    public Model save(Model model) {
        return modelRepository.save(model);
    }

    public Collection<Object> getAllModelsWithParameter(Parameter parameter) {
        return modelRepository.getAllByParameters(parameter);
    }

    public void deleteById(Long id) {
        modelRepository.deleteById(id);
    }
    @Transactional
    public Model update(Long id, Model model) {
        Model existingModel = modelRepository.findById(id).orElseThrow(() -> new RuntimeException("Model not found"));
        List<Parameter> modifiableParameters = new ArrayList<>(model.getParameters()); // coping to avoid UnsupportedOperationException

        existingModel.setName(model.getName());
        existingModel.setDescription(model.getDescription());
        existingModel.setParameters(modifiableParameters);

        return save(existingModel);
    }
}
