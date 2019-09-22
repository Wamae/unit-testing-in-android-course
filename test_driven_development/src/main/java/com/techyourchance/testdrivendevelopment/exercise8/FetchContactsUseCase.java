package com.techyourchance.testdrivendevelopment.exercise8;

import com.techyourchance.testdrivendevelopment.exercise8.contacts.Contact;
import com.techyourchance.testdrivendevelopment.exercise8.networking.ContactSchema;
import com.techyourchance.testdrivendevelopment.exercise8.networking.GetContactsHttpEndpoint;

import java.util.ArrayList;
import java.util.List;

public class FetchContactsUseCase {

    private List<Listener> listeners = new ArrayList<>();
    private GetContactsHttpEndpoint fetchUserHttpEndpointSync;

    FetchContactsUseCase(GetContactsHttpEndpoint fetchUserHttpEndpointSync) {
        this.fetchUserHttpEndpointSync = fetchUserHttpEndpointSync;
    }

    void fetchContact(String userId) {
        fetchUserHttpEndpointSync.getContacts(userId, new GetContactsHttpEndpoint.Callback() {

            @Override
            public void onGetContactsSucceeded(List<ContactSchema> contactSchemaList) {
                for (Listener listener : listeners) {
                    listener.onContactFetched(contactsFromSchema(contactSchemaList));
                }
            }

            @Override
            public void onGetContactsFailed(GetContactsHttpEndpoint.FailReason failReason) {
                if (failReason == GetContactsHttpEndpoint.FailReason.GENERAL_ERROR ||
                        failReason == GetContactsHttpEndpoint.FailReason.NETWORK_ERROR) {
                    for (Listener listener : listeners) {
                        listener.onFetchContactsFailed();
                    }
                }
            }
        });
    }

    private List<Contact> contactsFromSchema(List<ContactSchema> contactSchemaList) {
        List<Contact> contacts = new ArrayList<>();

        for (ContactSchema schema : contactSchemaList) {
            contacts.add(new Contact(schema.getId(), schema.getFullName(), schema.getImageUrl()));
        }
        return contacts;
    }

    void registerListener(Listener listener) {
        listeners.add(listener);
    }

    public void unregisterListener(Listener listenerMock2) {
        listeners.remove(listenerMock2);
    }

    public interface Listener {
        void onContactFetched(List<Contact> capture);

        void onFetchContactsFailed();
    }
}
