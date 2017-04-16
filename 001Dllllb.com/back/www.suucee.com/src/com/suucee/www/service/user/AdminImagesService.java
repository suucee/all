package com.suucee.www.service.user;

import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Service;


@Service
public interface AdminImagesService {
    public int addImage(int id,String imgPath,String imgName,String imgType,HttpSession session);
    public boolean removeProof(int id, HttpSession session);
}
