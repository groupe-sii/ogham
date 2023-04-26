package fr.sii.ogham.maven.shade.plugin.transformer;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Include {
    private String include;

    public boolean matches(String resource) {
        return resource.startsWith(include);
    }

    public void set(String include) {
        this.include = include;
    }
}
