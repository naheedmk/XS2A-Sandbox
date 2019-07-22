package de.adorsys.psd2.sandbox.tpp.rest.server.mapper;

import de.adorsys.ledgers.middleware.api.domain.um.AccessTypeTO;
import de.adorsys.ledgers.middleware.api.domain.um.UserTO;
import de.adorsys.psd2.sandbox.tpp.rest.api.domain.*;
import org.junit.Assert;
import org.junit.Test;
import org.mapstruct.factory.Mappers;

import java.util.Arrays;
import java.util.Collection;

public class UserMapperTest {
    private final UserMapper userMapper = Mappers.getMapper(UserMapper.class);

    @Test
    public void ttpInfoToUserTOTest() {
        TppInfo tppInfo = new TppInfo();
        tppInfo.setEmail("vne@adorsys.de");
        tppInfo.setLogin("vne");
        tppInfo.setPin("12345");
        tppInfo.setId("12345678");

        UserTO user = userMapper.ttpInfoToUserTO(tppInfo);

        Assert.assertEquals(user.getEmail(), tppInfo.getEmail());
        Assert.assertEquals(user.getLogin(), tppInfo.getLogin());
        Assert.assertEquals(user.getPin(), tppInfo.getPin());
    }

    @Test
    public void toUserTO() {
        User user = new User();
        user.setEmail("vne@adorsys.de");
        user.setLogin("vne");
        user.setPin("12345");

        ScaUserData sca =  new ScaUserData();
        sca.setMethodValue("vne@adorsys.de");
        sca.setScaMethod(ScaMethodType.EMAIL);

//        AccountAccess accountAccess = new AccountAccess();
//        accountAccess.setIban("DE1234567890");
//        accountAccess.setAccessType(AccessTypeTO.OWNER);
//        accountAccess.setScaWeight(50);

        user.setScaUserData(Arrays.asList(sca));
        user.setUserRoles(Arrays.asList(UserRole.CUSTOMER));


        UserTO userTO = userMapper.toUserTO(user);

        Assert.assertEquals(userTO.getEmail(), user.getEmail());
        Assert.assertEquals(userTO.getLogin(), user.getLogin());
        Assert.assertEquals(userTO.getPin(), user.getPin());
        Assert.assertEquals(userTO.getScaUserData().size(), user.getScaUserData().size());
        Assert.assertEquals(userTO.getScaUserData().get(0).getMethodValue(), user.getScaUserData().get(0).getMethodValue());
        Assert.assertEquals(userTO.getScaUserData().get(0).getScaMethod().toString(), user.getScaUserData().get(0).getScaMethod().toString());
    }
}
