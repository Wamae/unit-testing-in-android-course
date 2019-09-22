package com.techyourchance.testdrivendevelopment.exercise8;

import com.techyourchance.testdrivendevelopment.exercise8.contacts.Contact;
import com.techyourchance.testdrivendevelopment.exercise8.networking.ContactSchema;
import com.techyourchance.testdrivendevelopment.exercise8.networking.GetContactsHttpEndpoint;
import com.techyourchance.testdrivendevelopment.exercise8.networking.GetContactsHttpEndpoint.Callback;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@RunWith(MockitoJUnitRunner.class)
public class FetchContactsUseCaseTest {

    private static final String ID = "id";
    private static final String FULL_NAME = "John Doe";
    private static final String IMAGE_URL = "image url";
    private static final String PHONE_NUMBER = "phone number";
    private static final double AGE = 29;
    private FetchContactsUseCase SUT;
    @Mock
    FetchContactsUseCase.Listener listenerMock1;
    @Mock
    FetchContactsUseCase.Listener listenerMock2;
    @Mock
    GetContactsHttpEndpoint getContactsHttpEndpointMock;
    @Captor
    ArgumentCaptor<List<Contact>> argumentCaptor;

    @Before
    public void setUp() throws Exception {

        SUT = new FetchContactsUseCase(getContactsHttpEndpointMock);
        success();
    }

    private void success() {
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                Callback callback = (Callback)args[1];
                callback.onGetContactsSucceeded(getContactsSchemas());
                return null;
            }
        }).when(getContactsHttpEndpointMock).getContacts(any(String.class), any(Callback.class));
    }

    @Test
    public void fetchContact_success_observersNotifiedWithCorrectData() {

        SUT.registerListener(listenerMock1);
        SUT.registerListener(listenerMock2);

        SUT.fetchContact(ID);

        verify(listenerMock1).onContactFetched(argumentCaptor.capture());
        verify(listenerMock2).onContactFetched(argumentCaptor.capture());

        List<List<Contact>> captures = argumentCaptor.getAllValues();
        List<Contact> capture1 = captures.get(0);
        List<Contact> capture2 = captures.get(1);

        assertThat(capture1, is(getContacts()));
        assertThat(capture2, is(getContacts()));
    }

    private List<Contact> getContacts() {
        List<Contact> contacts = new ArrayList<>();
        contacts.add(new Contact(ID, FULL_NAME, IMAGE_URL));

        return contacts;
    }

    private List<ContactSchema> getContactsSchemas() {
        List<ContactSchema> contacts = new ArrayList<>();
        contacts.add(new ContactSchema(ID, FULL_NAME,PHONE_NUMBER, IMAGE_URL,AGE));

        return contacts;
    }

    @Test
    public void fetchContact_generalError_observersNotifiedOfFailure(){
        generalError();

        SUT.registerListener(listenerMock1);
        SUT.registerListener(listenerMock2);

        SUT.fetchContact(ID);

        verify(listenerMock1).onFetchContactsFailed();
    }

    private void generalError() {
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                Callback callback = (Callback)args[1];
                callback.onGetContactsFailed(GetContactsHttpEndpoint.FailReason.GENERAL_ERROR);
                return null;
            }
        }).when(getContactsHttpEndpointMock).getContacts(any(String.class), any(Callback.class));
    }

    @Test
    public void fetchContact_networkError_observersNotifiedOfFailure(){
        networkError();

        SUT.registerListener(listenerMock1);
        SUT.registerListener(listenerMock2);

        SUT.fetchContact(ID);

        verify(listenerMock1).onFetchContactsFailed();
    }

    private void networkError() {
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                Callback callback = (Callback)args[1];
                callback.onGetContactsFailed(GetContactsHttpEndpoint.FailReason.NETWORK_ERROR);
                return null;
            }
        }).when(getContactsHttpEndpointMock).getContacts(any(String.class), any(Callback.class));
    }

    @Test
    public void fetchContact_unregisteredListerNotNotified(){
        SUT.registerListener(listenerMock2);

        SUT.unregisterListener(listenerMock2);

        verifyNoMoreInteractions(listenerMock2);

    }

}