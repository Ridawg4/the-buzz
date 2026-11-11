
package com.teamwhite.thebuzz.Repositories;

import com.teamwhite.thebuzz.model.Show;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ShowRepository extends CrudRepository<Show, UUID> {

}
