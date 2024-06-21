package com.michael.document.entity;

import com.fasterxml.jackson.annotation.*;
import com.michael.document.entity.base.Auditable;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Entity
@Table(name = "credentials")
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class CredentialEntity extends Auditable {

    private String password;

    @OneToOne(targetEntity = UserEntity.class, fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
    @JsonIdentityReference(alwaysAsId = true)
    @JsonProperty("user_id")
    private UserEntity userEntity;
}


//@OneToOne: Указывает, что между CredentialEntity и UserEntity существует отношение один-к-одному.
//        targetEntity = UserEntity.class: Указывает, что связанная сущность — это UserEntity.
//        fetch = FetchType.EAGER: Задает стратегию загрузки данных. В данном случае связанная сущность будет загружаться немедленно вместе с основной сущностью CredentialEntity.
//@JoinColumn(name = "user_id", nullable = false)
//
//@JoinColumn: Определяет столбец, который будет использоваться для внешнего ключа. В данном случае столбец называется user_id.
//        name = "user_id": Имя столбца в таблице базы данных.
//        nullable = false: Указывает, что это поле не может быть null, т.е. каждый CredentialEntity должен быть связан с UserEntity.
//@OnDelete(action = OnDeleteAction.CASCADE)
//
//@OnDelete: Определяет поведение при удалении связанной сущности.
//        action = OnDeleteAction.CASCADE: Указывает, что если UserEntity будет удален, то связанные CredentialEntity также будут удалены.
//@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
//
//@JsonIdentityInfo: Используется для обработки циклических ссылок при сериализации объектов в JSON.
//        generator = ObjectIdGenerators.PropertyGenerator.class: Определяет, что идентификатором объекта будет одно из его свойств.
//        property = "id": Указывает, что в качестве идентификатора будет использоваться свойство id объекта UserEntity.
//@JsonIdentityReference(alwaysAsId = true)
//
//@JsonIdentityReference: Указывает, как следует сериализовать объект-ссылку.
//        alwaysAsId = true: Задает, что объект UserEntity будет сериализован только как его идентификатор, а не как полный объект.
//@JsonProperty("user_id")
//
//@JsonProperty: Определяет, как поле будет называться в JSON.
//        "user_id": Указывает, что в JSON поле будет называться user_id.
