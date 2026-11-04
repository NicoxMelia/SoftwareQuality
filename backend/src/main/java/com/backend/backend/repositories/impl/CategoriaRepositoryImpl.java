package com.backend.backend.repositories.impl;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import com.backend.backend.dto.CategoriaDTO;
import com.backend.backend.firebase.FirebaseInitializer;
import com.backend.backend.repositories.CategoriaRepository;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.firestore.WriteResult;

@Repository
@Profile("!k8s")
public class CategoriaRepositoryImpl implements CategoriaRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(CategoriaRepositoryImpl.class);

    private CollectionReference categoriasReference;

    public CategoriaRepositoryImpl(FirebaseInitializer firebaseInitializer) {
        this.categoriasReference = firebaseInitializer.getFirestore().collection("categorias");
    }

    @Override
    public List<CategoriaDTO> list() {
        List<CategoriaDTO> res = new ArrayList<>();
        CategoriaDTO categoria;

        ApiFuture<QuerySnapshot> query = categoriasReference.get();
        try {
            for (DocumentSnapshot doc : query.get().getDocuments()) {
                categoria = doc.toObject(CategoriaDTO.class);
                categoria.setIdCategoria(doc.getId());
                res.add(categoria);
            }
            return res;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return List.of();
        } catch (Exception e) {
            return List.of();
        }
    }

    @Override
    public CategoriaDTO insert(CategoriaDTO categoria) {
        DocumentReference docRef = this.categoriasReference.document();
        ApiFuture<WriteResult> wrApi = docRef.set(categoria);
        try {
            if (null != wrApi.get()) {
                return get(docRef.getId());
            }
            return null;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public CategoriaDTO get(String idCategoria) {
        DocumentReference categoriaRef = categoriasReference.document(idCategoria);
        try {
            CategoriaDTO ret = categoriaRef.get().get().toObject(CategoriaDTO.class);
            ret.setIdCategoria(categoriaRef.getId());
            return ret;

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return null;
        } catch (Exception e) {
            LOGGER.warn("No se pudo consultar la categoría.");
            return null;
        }
    }
}
