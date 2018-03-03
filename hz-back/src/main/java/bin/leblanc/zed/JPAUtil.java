package bin.leblanc.zed;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.jpa.internal.metamodel.EntityTypeImpl;
import org.hibernate.jpa.internal.metamodel.PluralAttributeImpl;
import org.hibernate.jpa.internal.metamodel.SingularAttributeImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.persistence.metamodel.Attribute;
import javax.persistence.metamodel.PluralAttribute;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@Slf4j
public class JPAUtil {

    @Autowired
    EntityManager entityManager;

    /**
     * 得到一个实体类的默认ID，如果没有则会抛出异常
     * @param root
     * @return
     */
    public String getIdName(Root root){
         return  ((SingularAttributeImpl)(root
                    .getModel()
                    .getDeclaredAttributes()
                    .stream()
                    .filter(attr -> (attr instanceof  SingularAttributeImpl) && ((SingularAttributeImpl) attr).isId())
                    .findFirst()
                    .get()))
                 .getName();
    }

    public String getIdName(Class clz){
        return getIdName(getRoot(clz));
    }



    public Root getRoot(Class clz){
        return entityManager.getCriteriaBuilder().createQuery(clz).from(clz);
    }


    /**
     * 得到一个模型的所有附加字段
     * @param clz
     * @return
     */
    public Set<Attribute> getLinkFields(Class clz){
        Root root = getRoot(clz);
        return getLinkFields(root);
    }
    public Set<Attribute> getLinkFields(Root root){
        Set<Attribute> result = (Set<Attribute>) root.getModel().getDeclaredAttributes()
                .stream()
                .filter(attribute -> {
                    if (attribute instanceof PluralAttributeImpl) {
                        Attribute.PersistentAttributeType type = ((PluralAttributeImpl) attribute).getPersistentAttributeType();
                        return type == Attribute.PersistentAttributeType.MANY_TO_MANY || type == Attribute.PersistentAttributeType.MANY_TO_ONE || type == Attribute.PersistentAttributeType.ONE_TO_MANY || type == Attribute.PersistentAttributeType.ONE_TO_ONE;
                    } else if (attribute instanceof SingularAttributeImpl) {
                        Attribute.PersistentAttributeType type = ((SingularAttributeImpl) attribute).getPersistentAttributeType();
                        return type == Attribute.PersistentAttributeType.MANY_TO_MANY || type == Attribute.PersistentAttributeType.MANY_TO_ONE || type == Attribute.PersistentAttributeType.ONE_TO_MANY || type == Attribute.PersistentAttributeType.ONE_TO_ONE;
                    }
                    return false;
                }).collect(Collectors.toSet());
        return result;
    }

    public Set<Attribute> getAllFields(Root root){
        return root.getModel().getDeclaredAttributes();
    }

    /**
     * 得到一个模型除附加字段的所有字段
     * @param clz
     * @return
     */
    public Set<Attribute> getNormalFields(Class clz){
        Root root = getRoot(clz);
        return getNormalFields(root);
    }

    public Set<Attribute> getNormalFields(Root root){
        return root.getModel().getDeclaredAttributes();
    }


    /**
     * 得到合法的附加字段
     * @return
     */
    public Set<String> getAvaExternFields(Class clz, Set<String> fields){
        Root root = getRoot(clz);
        return getAvaExternFields(root,fields);
    }

    public Set<String> getAvaExternFields(Root root, Set<String> fields){
        Set<Attribute> linkFields = getLinkFields(root);
        return linkFields.stream()
                .filter(item -> fields.contains(item.getName()))
                .map(item -> item.getName())
                .collect(Collectors.toSet());
    }


//    public Set<String> getAllFields(Class clz){
//        return getAllFields(getRoot(clz));
//    }
//
//    public Set<String> getAllFields(Root root){
//        Set<Attribute> normalFields = getNormalFields(root);
//        Set<PluralAttribute> linkFields = getLinkFields(root);
//        Set<String> result = normalFields
//                .stream()
//                .map(field -> field.getName())
//                .collect(Collectors.toSet());
//        linkFields.forEach(field -> {
//            result.add(field.getName());
//        });
//        return result;
//    }

}
