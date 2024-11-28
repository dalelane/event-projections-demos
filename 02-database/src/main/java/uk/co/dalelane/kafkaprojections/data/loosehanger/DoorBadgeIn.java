/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package uk.co.dalelane.kafkaprojections.data.loosehanger;

import java.io.Serializable;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Id;
import jakarta.persistence.Column;

@Entity
@Table(name="doorbadgeins")
@NamedQuery(
    name = "DoorBadgeIn.findBadgeIn",
    query = "SELECT e FROM DoorBadgeIn e WHERE e.door = :door"
)
public class DoorBadgeIn implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull
    @Id
    @Column(name = "door")
    String door;

    @NotNull
    @Column(name = "recordid")
    String recordid;

    @NotNull
    @Column(name = "employee")
    String employee;

    @NotNull
    @Column(name = "badgetime")
    String badgetime;

    public DoorBadgeIn() {}

    public String getDoorId() {
        return door;
    }

    public void setDoorId(String door) {
        this.door = door;
    }

    public String getRecordId() {
        return recordid;
    }

    public void setRecordId(String recordid) {
        this.recordid = recordid;
    }

    public String getEmployee() {
        return employee;
    }

    public void setEmployee(String employee) {
        this.employee = employee;
    }

    public String getBadgeTime() {
        return badgetime;
    }

    public void setBadgeTime(String badgetime) {
        this.badgetime = badgetime;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((door == null) ? 0 : door.hashCode());
        result = prime * result + ((recordid == null) ? 0 : recordid.hashCode());
        result = prime * result + ((employee == null) ? 0 : employee.hashCode());
        result = prime * result + ((badgetime == null) ? 0 : badgetime.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        DoorBadgeIn other = (DoorBadgeIn) obj;
        if (door == null) {
            if (other.door != null)
                return false;
        } else if (!door.equals(other.door))
            return false;
        if (recordid == null) {
            if (other.recordid != null)
                return false;
        } else if (!recordid.equals(other.recordid))
            return false;
        if (employee == null) {
            if (other.employee != null)
                return false;
        } else if (!employee.equals(other.employee))
            return false;
        if (badgetime == null) {
            if (other.badgetime != null)
                return false;
        } else if (!badgetime.equals(other.badgetime))
            return false;
        return true;
    }
}
