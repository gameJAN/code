package com.biyesheji.code.repository;

import com.biyesheji.code.entity.ArcType;
import com.biyesheji.code.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
/*资源类型Repository*/
public interface ArcTypeRepository extends JpaRepository<ArcType,Integer>, JpaSpecificationExecutor<ArcType> {

}
