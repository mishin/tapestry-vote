package net.tap5.vote.pages;

import java.util.Date;
import java.util.List;

import org.apache.tapestry5.Block;
import org.apache.tapestry5.EventContext;
import org.apache.tapestry5.SymbolConstants;
import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.annotations.Log;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.hibernate.annotations.CommitAfter;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.annotations.Symbol;
import org.apache.tapestry5.services.HttpError;
import org.apache.tapestry5.services.ajax.AjaxResponseRenderer;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.slf4j.Logger;

import net.tap5.vote.entities.Item;

/**
 * Start page of application vote.
 */
public class Index {
	@Property
	private Item newItem;

	@Property
	private Item item;

	@Inject
	private Session session;

	@Inject
	private Logger logger;

	@Inject
	private AjaxResponseRenderer ajaxResponseRenderer;

	@Property
	@Inject
	@Symbol(SymbolConstants.TAPESTRY_VERSION)
	private String tapestryVersion;

	@InjectPage
	private About about;

	@Inject
	private Block block;

	// Handle call with an unwanted context
	Object onActivate(EventContext eventContext) {
		return eventContext.getCount() > 0 ? new HttpError(404, "Resource not found") : null;
	}

	Object onActionFromLearnMore() {
		about.setLearn("LearnMore");

		return about;
	}

	@Log
	void onComplete() {
		logger.info("Complete call on Index page");
	}

	@Log
	void onAjax() {
		logger.info("Ajax call on Index page");

		ajaxResponseRenderer.addRender("middlezone", block);
	}

	@CommitAfter
	public void onSuccess() {
		session.persist(newItem);
	}

	@SuppressWarnings("unchecked")
	public List<Item> getItems() {
		return session.createCriteria(Item.class).addOrder(Order.desc("votes")).list();
	}

	@CommitAfter
	public void onActionFromVote(Item i) {
		i.setVotes(i.getVotes() + 1);
		session.persist(i);
	}

	public Date getCurrentTime() {
		return new Date();
	}
}
