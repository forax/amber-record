# Dockerfile for java guide
FROM forax/amber-record

USER jovyan

# Launch the notebook server
ENV IJAVA_COMPILER_OPTS "--enable-preview --source=15"
WORKDIR $HOME/slideshow
CMD ["jupyter", "notebook", "--no-browser", "--ip", "0.0.0.0"]

