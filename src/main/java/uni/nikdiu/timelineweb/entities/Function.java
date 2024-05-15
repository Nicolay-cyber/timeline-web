package uni.nikdiu.timelineweb.entities;

import jakarta.annotation.PostConstruct;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Collection;
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
    @JoinTable(name = "parameter_function",
            joinColumns = @JoinColumn(name = "parameter_id"),
            inverseJoinColumns = @JoinColumn(name = "function_id"))
    private List<Parameter> relatedParameters;

    @Transient
    private List<String> expression;

    @PostConstruct
    private void extractExpression() {
        expression = Arrays.asList(stringExpression.split(" "));
    }

    @PostConstruct
    private void check() {
        log.debug("expression " + expression);
    }
/*
    @OneToMany(mappedBy = "function", cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    private List<Parameter> relatedParameters;


*/
}
