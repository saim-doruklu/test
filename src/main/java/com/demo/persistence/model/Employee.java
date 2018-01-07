package com.demo.persistence.model;

import com.google.common.collect.Sets;
import org.hibernate.envers.Audited;
import org.jboss.logging.Logger;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.Set;

@Entity
@NamedQuery(name = "Employee.findAll", query = "SELECT e FROM Employee e")
@Audited
public class Employee implements Serializable {

    private static Logger logger = Logger.getLogger(Employee.class);

    public enum OPERATION {
        INSERT, UPDATE, DELETE;
        private String value;

        OPERATION() {
            value = toString();
        }

        public String getValue() {
            return value;
        }

        public static OPERATION parse(final String value) {
            OPERATION operation = null;
            for (final OPERATION op : OPERATION.values()) {
                if (op.getValue().equals(value)) {
                    operation = op;
                    break;
                }
            }
            return operation;
        }
    };

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private int id;

    @Column(name = "name")
    private String name;

    @OneToMany(mappedBy = "boss", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @OrderBy("NAME DESC")
    private Set<Employee> workers = Sets.newHashSet();

    @Column(name = "operation")
    private String operation;

    @Column(name = "timestamp")
    private long timestamp;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<Employee> getWorkers() {
        return workers;
    }

    public void setWorkers(Set<Employee> workers) {
        this.workers = workers;
    }

    public OPERATION getOperation() {
        return OPERATION.parse(operation);;
    }

    public void setOperation(OPERATION operation) {
        this.operation = operation.getValue();
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final Employee other = (Employee) obj;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        return true;
    }

    @PrePersist
    public void onPrePersist() {
        logger.info("@PrePersist");
        audit(OPERATION.INSERT);
    }

    @PreUpdate
    public void onPreUpdate() {
        logger.info("@PreUpdate");
        audit(OPERATION.UPDATE);
    }

    @PreRemove
    public void onPreRemove() {
        logger.info("@PreRemove");
        audit(OPERATION.DELETE);
    }

    private void audit(final OPERATION operation) {
        setOperation(operation);
        setTimestamp((new Date()).getTime());
    }
}
