package com.SCU.pose.repository;
import com.SCU.pose.model.FlutterKicks;


import com.SCU.pose.model.Image;
import com.SCU.pose.model.Plank;
import com.SCU.pose.model.PushUp;
import com.SCU.pose.model.SitUp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FlutterKicksRepository extends JpaRepository<FlutterKicks, Integer> {
    // Define additional custom methods here if needed
}
