package uni.nikdiu.timelineweb.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@Entity
@Table(name = "functions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Function {
    @Transient
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "start_point")
    private Double startPoint;

    @Column(name = "end_point")
    private Double endPoint;

    @Column(name = "expression")
    private String stringExpression;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_parameter_id")
    private Parameter parentParameter;

    @ManyToMany
    @JoinTable(
            name = "function_parameters",
            joinColumns = @JoinColumn(name = "function_id"),
            inverseJoinColumns = @JoinColumn(name = "parameter_id")
    )
    private List<Parameter> relatedParameters;

    @Transient
    private List<String> expression;

    public Function(Long id, Double startPoint, Double endPoint, String stringExpression, List<String> expression) {
        this.id = id;
        this.startPoint = startPoint;
        this.endPoint = endPoint;
        this.stringExpression = stringExpression;
        this.expression = expression;
    }

    @Override
    public String toString() {
        return "Function " + id + " " + expression;
    }

}
