package uni.nikdiu.timelineweb.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "parameters")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Parameter {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "tag")
    private String tag;

    @Column(name = "description")
    private String description;

    @Column(name = "abbreviation")
    private String abbreviation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "unit_id")
    private Unit unit;

    @OneToMany(mappedBy = "parentParameter", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Function> functions;

    @OneToMany(mappedBy = "parentParameter", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Point> points;
    @ManyToMany
    @JoinTable(
            name = "function_parameters",
            joinColumns = @JoinColumn(name = "parameter_id"),
            inverseJoinColumns = @JoinColumn(name = "function_id")
    )
    private List<Function> relatedFunctions;

    public Parameter(Long id, String name, String tag, String description, String abbreviation) {
        this.id = id;
        this.name = name;
        this.tag = tag;
        this.description = description;
        this.abbreviation = abbreviation;
        this.points = new ArrayList<>();
        this.functions = new ArrayList<>();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Parameter{")
                .append("id=").append(id)
                .append(", name='").append(name).append('\'')
                .append(", description='").append(description).append('\'')
                .append(", abbreviation='").append(abbreviation).append('\'')
                .append(", unit=").append(unit != null ? unit.getName() : null);

        // Append descriptions of incoming functions
        if (functions != null && !functions.isEmpty()) {
            sb.append(", functions=[");
            for (Function function : functions) {
                sb.append("{").append(function).append("'}, ");
            }
            sb.delete(sb.length() - 2, sb.length()); // Remove the last comma and space
            sb.append("]");
        } else {
            sb.append(", functions=[]");
        }

        // Append descriptions of incoming points
        if (points != null && !points.isEmpty()) {
            sb.append(", points=[");
            for (Point point : points) {
                sb.append("{").append(point).append("'}, ");
            }
            sb.delete(sb.length() - 2, sb.length()); // Remove the last comma and space
            sb.append("]");
        } else {
            sb.append(", points=[]");
        }

        sb.append('}');
        return sb.toString();
    }

}
