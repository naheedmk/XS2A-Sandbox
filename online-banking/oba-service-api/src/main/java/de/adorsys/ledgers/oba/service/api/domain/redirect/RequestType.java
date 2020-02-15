package de.adorsys.ledgers.oba.service.api.domain.redirect;

public enum RequestType {
    LOGIN {
        @Override
        public String resolveVariableByRequestType(RedirectContext context) {
            return null;
        }

        @Override
        public void setContextFieldByRequestType(RedirectContext context, Object object) {

        }
    },
    INIT {
        @Override
        public String resolveVariableByRequestType(RedirectContext context) {
            return null;
        }

        @Override
        public void setContextFieldByRequestType(RedirectContext context, Object object) {
            context.setObject(object);
        }
    },
    SELECT_SCA {
        @Override
        public String resolveVariableByRequestType(RedirectContext context) {
            return context.getSelectedScaMethodId();
        }

        @Override
        public void setContextFieldByRequestType(RedirectContext context, Object object) {
            context.setSelectedScaMethodId((String) object);
        }
    },
    VALIDATE {
        @Override
        public String resolveVariableByRequestType(RedirectContext context) {
            return context.getTan();
        }

        @Override
        public void setContextFieldByRequestType(RedirectContext context, Object object) {
            context.setTan((String) object);
        }
    },
    FAIL{
        @Override
        public String resolveVariableByRequestType(RedirectContext context) {
            return null;
        }

        @Override
        public void setContextFieldByRequestType(RedirectContext context, Object object) {

        }
    };

    public abstract String resolveVariableByRequestType(RedirectContext context);

    public abstract void setContextFieldByRequestType(RedirectContext context, Object object);
}
