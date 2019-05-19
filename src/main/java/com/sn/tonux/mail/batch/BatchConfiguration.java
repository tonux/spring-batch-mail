package com.sn.tonux.mail.batch;

import javax.mail.internet.MimeMessage;

import org.springframework.batch.core.*;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.launch.support.SimpleJobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;

import com.sn.tonux.mail.batch.model.User;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.Date;

@Configuration
@EnableBatchProcessing
@EnableScheduling
public class BatchConfiguration {

	@Autowired
	public JobBuilderFactory jobBuilderFactory;
	
	@Autowired
	public StepBuilderFactory stepBuilderFactory;

	@Autowired
	private SimpleJobLauncher jobLauncher;
	
	@Value("${spring.mail.username}")
	private String sender;

	@Value("${item.batch.data}")
	public String data;
	
	@Value("${item.batch.attachment}")
	private String attachment;
	
	@Value("${item.batch.notifications.email}")
	private String email;


	@Bean
	public FlatFileItemReader<User> reader() {
		FlatFileItemReader<User> reader = new FlatFileItemReader<>();
		reader.setResource(new FileSystemResource(data));
		reader.setLinesToSkip(1);
		reader.setLineMapper(new DefaultLineMapper<User>() {{
			setLineTokenizer(new DelimitedLineTokenizer() {{
				setNames(new String[] {"fullname", "password", "email"} );
			}});
			setFieldSetMapper(new BeanWrapperFieldSetMapper<User>(){{
				setTargetType(User.class);
			}});
		}});
		return reader;
	}
	
	@Bean
	public UserItemProcessor processor() {
		return new UserItemProcessor(sender, attachment);
	}
	
	@Bean
	public MailBatchItemWriter writer() {
		MailBatchItemWriter writer = new MailBatchItemWriter();
		return writer;
	}


    @Bean
    public JobExecutionListener listener() {
        return new JobCompletionNotificationListener(email);
    }


    @Bean
    public Job importUserJob() {
        return jobBuilderFactory.get("importUserJob")
                .incrementer(new RunIdIncrementer())
                .listener(listener())
                .flow(step1())
                .end()
                .build();
    }

    @Bean
    public Step step1() {
        return stepBuilderFactory.get("step1")
                .<User, MimeMessage> chunk(10)
                .reader(reader())
                .processor(processor())
                .writer(writer())
                .build();
    }


	@Scheduled(fixedDelay = 2000)
	public void scheduleFixedDelayTask() throws Exception {
		System.out.println("---->>>> test");

		System.out.println(" Job Started at :"+ new Date());
		JobParameters param = new JobParametersBuilder().addString("JobID",
				String.valueOf(System.currentTimeMillis())).toJobParameters();
		JobExecution execution = jobLauncher.run(importUserJob(), param);
		System.out.println("Job finished with status :" + execution.getStatus());

		jobBuilderFactory.get("importUserJob")
				.incrementer(new RunIdIncrementer())
				.listener(listener())
				.flow(step1())
				.end()
				.build();
	}

	@Bean
	public SimpleJobLauncher jobLauncher(JobRepository jobRepository) {
		SimpleJobLauncher launcher = new SimpleJobLauncher();
		launcher.setJobRepository(jobRepository);
		return launcher;
	}
}
