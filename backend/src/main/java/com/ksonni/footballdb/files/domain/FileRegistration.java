package com.ksonni.footballdb.files.domain;

import com.ksonni.footballdb.utils.MathUtils;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.MediaType;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.ZonedDateTime;
import java.util.Objects;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor(force = true)
@Table(name = "files")
public class FileRegistration {

    @Id
    private String id;

    private String name;

    private String mimeType;

    private Long sizeBytes;

    private ZonedDateTime created;

    private String createdBy;

    /**
     * Converts mime type to Spring MediaType.
     *
     * @return parsed media type
     */
    public MediaType getMediaType() {
        return MediaType.valueOf(mimeType);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final FileRegistration that = (FileRegistration) o;

        if (!Objects.equals(id, that.id)) {
            return false;
        }
        if (!Objects.equals(name, that.name)) {
            return false;
        }
        if (!Objects.equals(mimeType, that.mimeType)) {
            return false;
        }
        if (!Objects.equals(sizeBytes, that.sizeBytes)) {
            return false;
        }
        if (!Objects.equals(created, that.created)) {
            return false;
        }
        return Objects.equals(createdBy, that.createdBy);
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = MathUtils.HASHING_PRIME * result + (name != null ? name.hashCode() : 0);
        result = MathUtils.HASHING_PRIME * result + (mimeType != null ? mimeType.hashCode() : 0);
        result = MathUtils.HASHING_PRIME * result + (sizeBytes != null ? sizeBytes.hashCode() : 0);
        result = MathUtils.HASHING_PRIME * result + (created != null ? created.hashCode() : 0);
        result = MathUtils.HASHING_PRIME * result + (createdBy != null ? createdBy.hashCode() : 0);
        return result;
    }

}
