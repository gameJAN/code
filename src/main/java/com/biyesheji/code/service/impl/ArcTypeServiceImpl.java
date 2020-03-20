package com.biyesheji.code.service.impl;


import com.biyesheji.code.entity.ArcType;
import com.biyesheji.code.repository.ArcTypeRepository;
import com.biyesheji.code.service.ArcTypeService;
import com.biyesheji.code.util.Consts;
import com.sun.javafx.scene.traversal.Direction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("arcTypeService")
public class ArcTypeServiceImpl  implements ArcTypeService {
    @Autowired
    private ArcTypeRepository arcTypeRepository;
    @Autowired
    private RedisTemplate<Object,Object> redisTemplate;
    @Override
    public List<ArcType> list(Integer page, Integer pageSize, Sort.Direction direction, String... properties) {
        Page<ArcType> arcTypePage = arcTypeRepository.findAll(PageRequest.of(page-1,pageSize,direction,properties));
        return arcTypePage.getContent();
    }

    @Override
    public List listAll(Sort.Direction direction, String... properties) {
        if (redisTemplate.hasKey(Consts.ALL_ARC_TYPE_NAME)){
            return redisTemplate.opsForList().range(Consts.ALL_ARC_TYPE_NAME,0,-1);
        }else {
            List list = arcTypeRepository.findAll(Sort.by(direction,properties));
            if(list!=null&&list.size()>0){
                for (int i = 0; i < list.size(); i++) {
                    redisTemplate.opsForList().rightPush(Consts.ALL_ARC_TYPE_NAME,(ArcType)list.get(i));

                }
            }
            return list;
        }
    }

    @Override
    public Long getCount() {
        return arcTypeRepository.count();
    }

    @Override
    public void save(ArcType arcType) {
        boolean flag = false;
        if (arcType.getArtTypeId()==null){
            flag = true;
        }
        arcTypeRepository.save(arcType);
        if (flag == true){
            redisTemplate.opsForList().rightPush(Consts.ALL_ARC_TYPE_NAME,arcType);
        }

    }

    @Override
    public void delete(Integer id) {
        arcTypeRepository.deleteById(id);

    }

    @Override
    public ArcType getById(Integer id) {
        return arcTypeRepository.getOne(id);
    }
}
