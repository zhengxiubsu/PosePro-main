
package com.SCU.pose.repository;

import com.SCU.pose.model.Image;
import com.SCU.pose.model.PushUp;
import com.SCU.pose.model.SitUp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SitUpRepository extends JpaRepository<SitUp, Integer> {
    // Define additional custom methods here if needed
}
